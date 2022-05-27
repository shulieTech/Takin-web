package io.shulie.takin.cloud.biz.notify.processor.calibration;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.shulie.takin.adapter.api.entrypoint.excess.ExcessTaskApi;
import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.notify.PressureEventCenter.AmdbCalibrationException;
import io.shulie.takin.cloud.biz.notify.PressureEventCenter.CloudCalibrationException;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.trace.DataCalibrationDTO;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import jodd.util.Bits;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PressureDataCalibration {

    @Resource
    private AppConfig appConfig;
    @Resource
    private TraceClient traceClient;
    @Resource
    private ExcessTaskApi excessTaskApi;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private ReportDao reportDao;
    @Resource
    private PressureTaskDAO pressureTaskDAO;

    // 数据校准事件
    @Async("dataCalibration")
    @Retryable(value = AmdbCalibrationException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void dataCalibrationAmdb(TaskResult result) {
        fillContext(result);
        try {
            callAmdb(result);
            dataCalibration_ing(result.getTaskId(), false);
        } catch (Exception e) {
            throw new AmdbCalibrationException(e.getMessage());
        }
    }

    @Async("dataCalibration")
    @Retryable(value = CloudCalibrationException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void dataCalibrationCloud(TaskResult result) {
        fillContext(result);
        try {
            callCloud(result);
            dataCalibration_ing(result.getTaskId(), true);
        } catch (Exception e) {
            throw new CloudCalibrationException(e.getMessage());
        }
    }

    @Recover
    public void dataCalibrationAmdbRecover(AmdbCalibrationException e, TaskResult result) {
        processCalibrationStatus(result.getTaskId(), false, e.getMessage(), false);
    }

    @Recover
    public void dataCalibrationCloudRecover(CloudCalibrationException e, TaskResult result) {
        processCalibrationStatus(result.getTaskId(), false, e.getMessage(), true);
    }

    // 设置数据校准中
    private void dataCalibration_ing(Long jobId, boolean cloud) {
        int offset = cloud ? 2 : 0;
        redisClientUtil.setBit(PressureStartCache.getDataCalibrationStatusKey(jobId), offset + 1, true);
        updateReport(jobId);
    }

    private void updateReport(Long jobId) {
        ReportResult report = reportDao.selectByJobId(jobId);
        if (Objects.nonNull(report)) {
            String messageKey = PressureStartCache.getDataCalibrationMessageKey(jobId);
            Map<Object, Object> message = redisClientUtil.hmget(messageKey);
            Map<String, String> map = Maps.newHashMap();
            String calibrationMessage = report.getCalibrationMessage();
            if (StringUtils.isNotBlank(calibrationMessage)) {
                map.putAll(JsonHelper.string2Obj(calibrationMessage, new TypeReference<Map<String, String>>() {
                }));
            }
            message.forEach((key, value) -> map.put(String.valueOf(key), String.valueOf(value)));
            report.setCalibrationMessage(GsonUtil.gsonToString(map));
            report.setCalibrationStatus(getStatus(jobId));
            ReportUpdateParam param = BeanUtil.copyProperties(report, ReportUpdateParam.class);
            reportDao.updateReport(param);
        }
    }

    private int getStatus(Long jobId) {
        BitFieldSubCommands commands = BitFieldSubCommands.create()
            .get(BitFieldType.unsigned(4)).valueAt(0);
        String statusKey = PressureStartCache.getDataCalibrationStatusKey(jobId);
        return redisClientUtil.getBit(statusKey, commands).get(0).intValue();
    }

    /**
     * amdb	    cloud
     * 00		00      未校准
     * 01		01      校准中
     * 10		10      校准失败
     * 11		11      校准成功
     */
    // 设置数据校准成功/失败
    protected void processCalibrationStatus(Long jobId, boolean success, String message, boolean cloud) {
        String type = cloud ? "cloud" : "amdb";
        int offset = cloud ? 2 : 0;
        String statusKey = PressureStartCache.getDataCalibrationStatusKey(jobId);
        redisClientUtil.setBit(statusKey, offset, true);
        redisClientUtil.setBit(statusKey, offset + 1, success);
        if (!success) {
            redisClientUtil.hmset(PressureStartCache.getDataCalibrationMessageKey(jobId), type, message);
        }
        updateReport(jobId);
        /**
         * 位数右往左数
         * int中第n位为0 ：(int & (1 << (n - 1))) != 0
         * int中第x、y位为0 ：(int & (1 << (x - 1) + 1 << (y - 1))) != 0
         * (1 << (2 - 1)) + (1 << (4 -1)) = 10
         */
        if (Bits.isSet(getStatus(jobId), 10)) { // 此处判断第2、4位为1，完成校验
            redisClientUtil.del(statusKey,
                PressureStartCache.getDataCalibrationMessageKey(jobId),
                PressureStartCache.getDataCalibrationLockKey(jobId));
        }
    }

    private void callAmdb(TaskResult result) {
        DataCalibrationDTO dto = new DataCalibrationDTO();
        dto.setJobId(result.getTaskId());
        dto.setResourceId(result.getResourceId());
        dto.setCallbackUrl(appConfig.getCallbackUrl());
        traceClient.dataCalibration(dto);
    }

    private void callCloud(TaskResult result) {
        DataCalibrationRequest request = new DataCalibrationRequest();
        request.setJobId(result.getTaskId());
        request.setResourceId(result.getResourceId());
        excessTaskApi.dataCalibration(request);
    }

    private void fillContext(TaskResult result) {
        TenantCommonExt context = new TenantCommonExt();
        context.setTenantId(result.getTenantId());
        context.setTenantAppKey(result.getUserAppKey());
        context.setEnvCode(result.getEnvCode());
        context.setTenantCode(result.getTenantCode());
        context.setSource(ContextSourceEnum.JOB_SCENE.getCode());
        WebPluginUtils.setTraceTenantContext(context);
    }
}
