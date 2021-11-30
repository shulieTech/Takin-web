package io.shulie.takin.web.biz.utils;

import java.util.List;

import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;
import io.shulie.takin.web.ext.util.E2ePluginUtils;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class E2eExceptionConfigInfoUtil {

    public static List<E2eExceptionConfigInfoExt> getConfig() {
        List<E2eExceptionConfigInfoExt> bottleneckConfig = null;
        if (WebPluginUtils.checkUserPlugin() && E2ePluginUtils.checkE2ePlugin()) {
            bottleneckConfig = E2ePluginUtils.getExceptionConfig(WebPluginUtils.traceTenantId(),
                WebPluginUtils.traceEnvCode());
        }
        return bottleneckConfig;
    }
}
