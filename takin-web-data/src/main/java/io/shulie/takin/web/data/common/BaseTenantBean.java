package io.shulie.takin.web.data.common;

import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author caijianying
 */
@Getter
@Setter
public class BaseTenantBean {
    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 环境编码
     */
    private String envCode;

    public BaseTenantBean(){
        this.envCode = WebPluginUtils.traceEnvCode();
        this.tenantId = WebPluginUtils.traceTenantId();
        System.out.println("go!");
    }
}
