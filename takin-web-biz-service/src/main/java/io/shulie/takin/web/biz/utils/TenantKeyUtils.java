package io.shulie.takin.web.biz.utils;

import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author 肖建璋
 * @date 2021/09/26/2:31 下午
 */
public class TenantKeyUtils {

    public static String getTenantKey() {
        String builder = WebPluginUtils.traceTenantId()
            + ":"
            + WebPluginUtils.traceEnvCode()
            + ":";
        return builder;
    }
}
