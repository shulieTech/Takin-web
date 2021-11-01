package io.shulie.takin.web.data.param.dictionary;

import io.shulie.takin.web.data.common.BaseTenantBean;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author caijianying
 */
@Getter
@Setter
public class DictionaryParam{

    public DictionaryParam(){
        this.tenantId = WebPluginUtils.traceTenantId();
        this.envCode = WebPluginUtils.traceEnvCode();
    }

    /**
     * 字典别名
     */
    private String typeAlias;

    /**
     * 字典值ID
     */
    private String tDictionaryId;

    private String active;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 环境编码
     */
    private String envCode;

}
