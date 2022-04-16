package io.shulie.takin.cloud.biz.notify.processor.calibration;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.excess.ExcessTaskApi;
import io.shulie.takin.adapter.api.model.request.excess.DataCalibrationRequest;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.notify.PressureEventCenter.AmdbCalibrationException;
import io.shulie.takin.cloud.biz.notify.PressureEventCenter.CloudCalibrationException;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.trace.DataCalibrationDTO;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PressureDataCalibration extends AbstractIndicators {

    @Resource
    private AppConfig appConfig;
    @Resource
    private TraceClient traceClient;
    @Resource
    private ExcessTaskApi excessTaskApi;

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
