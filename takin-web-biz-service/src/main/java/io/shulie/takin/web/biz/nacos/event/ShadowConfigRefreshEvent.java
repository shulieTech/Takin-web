package io.shulie.takin.web.biz.nacos.event;

import io.shulie.takin.web.ext.util.WebPluginUtils;

public class ShadowConfigRefreshEvent {
    private String appName;

    /**
     * 租户id
     */
    private Long tenantId = WebPluginUtils.traceTenantId();

    /**
     * 环境编码
     */
    private String envCode = WebPluginUtils.traceEnvCode();

    private String userAppKey = WebPluginUtils.traceTenantAppKey();

    public ShadowConfigRefreshEvent(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public String getUserAppKey() {
        return userAppKey;
    }
}
