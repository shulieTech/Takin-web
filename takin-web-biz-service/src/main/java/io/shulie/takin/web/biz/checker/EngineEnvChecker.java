package io.shulie.takin.web.biz.checker;

import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanStatusRequest;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import io.shulie.takin.utils.json.JsonHelper;
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
                status = cloudWatchmanApi.status(new WatchmanStatusRequest());
            } catch (Exception e) {
                return CheckResult.fail(type(), "cloud环境异常");
            }
            log.info("调度器环境检测:{}", JsonHelper.bean2Json(status));
            if (Objects.isNull(status)) {
                return CheckResult.fail(type(), "调度器异常");
            }
            String message = status.getMessage();
            if (StringUtils.isNotBlank(message)) {
                return CheckResult.fail(type(), message);
            }
            if (isExpire(status)) {
                return CheckResult.fail(type(), "调度器时效异常");
            }
            return CheckResult.success(type());
        } catch (Exception e) {
            return CheckResult.fail(type(), e.getMessage());
        }
    }

    private boolean isExpire(WatchmanStatusResponse status) {
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
