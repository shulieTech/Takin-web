package io.shulie.takin.web.ext.entity.tenant;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: SwitchTenantExt
 * @Description: 切换租户类
 * @Date: 2021/10/20 17:14
 */
@Data
public class SwitchTenantExt {
    /**
     * source 租户code
     */
    private String sourceTenantCode;
    /**
     * 目标 租户code
     */
    private String targetTenantCode;
    /**
     * source 环境code
     */
    private String sourceEnvCode;
    /**
     * 目标 环境code
     */
    private String targetEnvCode;
}
