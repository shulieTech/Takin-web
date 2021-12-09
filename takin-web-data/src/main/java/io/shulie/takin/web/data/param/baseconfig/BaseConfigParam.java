package io.shulie.takin.web.data.param.baseconfig;

import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class BaseConfigParam {

    public BaseConfigParam(){
        this.tenantId = WebPluginUtils.traceTenantId();
        this.envCode = WebPluginUtils.traceEnvCode();
    }

    public BaseConfigParam(String configCode){
        this.configCode = configCode;
        this.tenantId = WebPluginUtils.traceTenantId();
        this.envCode = WebPluginUtils.traceEnvCode();
    }

    private String configCode;

    private Long tenantId;

    private String envCode;
}
