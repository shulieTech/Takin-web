package io.shulie.takin.web.app.conf.mybatis;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.app.conf.mybatis
 * @ClassName: TenantField
 * @Description: TODO
 * @Date: 2021/10/18 14:28
 */
public interface TenantField {
    /**
     * 租户
     */
    String FIELD_TENANT_ID = "tenantId";
    /**
     * 环境
     */
    String FIELD_ENV_CODE = "envCode";

    /**
     * 用户
     */
    String FIELD_USER_ID = "userId";

    /**
     * key
     */
    String FIELD_TENANT_APP_KEY = "tenantAppKey";
}
