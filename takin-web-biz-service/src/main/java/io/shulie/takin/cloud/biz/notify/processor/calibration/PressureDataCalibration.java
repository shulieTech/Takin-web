package io.shulie.takin.cloud.biz.notify.processor.calibration;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.notify.PressureEventCenter.AmdbCalibrationException;
import io.shulie.takin.cloud.biz.notify.PressureEventCenter.CloudCalibrationException;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.model.callback.Calibration.Data;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.BitFieldSubCommands.BitFieldType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.TaskScheduler;
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
    private PressureTaskApi pressureTaskApi;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private ReportDao reportDao;
    @Resource
    @Lazy
    private DataCalibrationProcessor dataCalibrationProcessor;
    @Resource(name = "schedulerPool")
    private TaskScheduler taskScheduler;
    @Value("${takin.data.calibration.time-out: 5}")
    private Integer dataCalibrationTimeOut;

    private static final String AMDB = "amdb";
    public static final String CLOUD = "cloud";
    private static final int AMDB_OFFSET = 0;
    private static final int CLOUD_OFFSET = 2;

    // 数据校准事件
    @Async("dataCalibration")
    @Retryable(value = AmdbCalibrationException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void dataCalibrationAmdb(TaskResult result) {
        fillContext(result);
        try {
            callAmdb(result);
            dataCalibration(result, false);
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
            dataCalibration(result, true);
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
    private void dataCalibration(TaskResult result, boolean cloud) {
        updateReport(result.getTaskId());
        registerFailOfTimeout(result, cloud);
    }

    // 校准超时失败
    private void registerFailOfTimeout(TaskResult result, boolean cloud) {
        Runnable r = () -> {
            fillContext(result);
            DataCalibrationNotifyParam param = new DataCalibrationNotifyParam();
            Data data = new Data();
            data.setCompleted(false);
            data.setPressureId(result.getTaskId());
            data.setResourceId(Long.parseLong(result.getResourceId()));
            param.setSource(ofType(cloud));
            data.setContent("校准超时失败");
            dataCalibrationProcessor.process(param);
        };
        // 校准超时失败
        taskScheduler.schedule(r, new Date(System.currentTimeMillis() + dataCalibrationTimeOut * 60 * 1000));
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
            ReportUpdateParam param = new ReportUpdateParam();
            param.setId(report.getId());
            param.setCalibrationMessage(GsonUtil.gsonToString(map));
            param.setCalibrationStatus(getStatus(jobId));
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
        int offset = offset(cloud);
        String statusKey = PressureStartCache.getDataCalibrationStatusKey(jobId);
        redisClientUtil.setBit(statusKey, offset, true);
        redisClientUtil.setBit(statusKey, offset + 1, success);
        if (!success) {
            redisClientUtil.hmset(PressureStartCache.getDataCalibrationMessageKey(jobId), ofType(cloud), message);
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
        request.setPressureId(result.getTaskId());
        request.setResourceId(result.getResourceId());
        pressureTaskApi.dataCalibration(request);
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

    private static String ofType(boolean cloud) {
        return cloud ? CLOUD : AMDB;
    }

    public static boolean isCloud(String source) {
        return CLOUD.equalsIgnoreCase(source);
    }

    public static int offset(boolean cloud) {
        return cloud ? CLOUD_OFFSET : AMDB_OFFSET;
    }
}
