package io.shulie.takin.web.ext.entity.tenant;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: TenantConfigExt
 * @Description: 租户配置插件
 * @Date: 2021/11/2 11:10
 */
@Data
public class TenantConfigExt {
    /**
     * 配置名
     */
    private String configKey;
    /**
     * 配置内容
     */
    private String configValue;

    /**
     * 配置说明
     */
    private String configDesc;
}
