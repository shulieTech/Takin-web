package io.shulie.takin.web.biz.checker;

import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanStatusRequest;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.job.PressureEnvInspectionJob.EngineEnvResponse;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EngineEnvChecker implements StartConditionChecker {

    @Resource
    private CloudWatchmanApi cloudWatchmanApi;
    private static final long EXPIRE_TIME = 5 * 60 * 1000;

    @Override
    public CheckResult check(StartConditionCheckerContext context) throws TakinCloudException {
        try {
            WatchmanStatusResponse status;
            try {
                WatchmanStatusRequest request = new WatchmanStatusRequest();
                request.setWatchmanId(context.getMachineId());
                status = cloudWatchmanApi.status(request);
            } catch (Exception e) {
                return CheckResult.fail(type(), "cloud环境异常");
            }
            log.info("调度器环境检测:{}", JsonHelper.bean2Json(status));
            EngineEnvResponse response = new EngineEnvResponse(
                EngineType.of(context.getMachineType()), context.getMachineId(), status);
            String message = extraErrorMessage(response);
            return message != null ? CheckResult.fail(type(), message) : CheckResult.success(type());
        } catch (Exception e) {
            return CheckResult.fail(type(), e.getMessage());
        }
    }

    public static String extraErrorMessage(EngineEnvResponse response) {
        String typeNameSuffix = "(" + response.getType().getName() + ")：";
        String engineId = response.getEngineId() + typeNameSuffix;
        WatchmanStatusResponse status = response.getStatus();
        if (Objects.isNull(status)) {
            return engineId + "调度器异常";
        }
        io.shulie.takin.cloud.model.resource.Resource resource = status.getResource();
        boolean resourceIsNull = Objects.isNull(resource);
        String namePrefix = resourceIsNull ? engineId : resource.getName() + typeNameSuffix;
        String message = StringUtils.defaultIfBlank(status.getMessage(), resourceIsNull ? "调度器不存在" : "");
        return StringUtils.isNotBlank(message) ? namePrefix + message : (isExpire(status) ? namePrefix + "调度器时效异常" : null);
    }

    private static boolean isExpire(WatchmanStatusResponse status) {
        return status.getTime() + EXPIRE_TIME < System.currentTimeMillis();
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public String type() {
        return "env";
    }
}
