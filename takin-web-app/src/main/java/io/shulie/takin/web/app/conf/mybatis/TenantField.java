package io.shulie.takin.web.app.conf.mybatis;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/10/18 14:28
 */
@AllArgsConstructor
@Getter
public enum TenantField {
    /**
     * 租户
     */
    FIELD_TENANT_ID("tenantId", "tenant_Id"),

    FIELD_DEPT_ID("deptId", "dept_id"),
    /**
     * 环境
     */
    FIELD_ENV_CODE("envCode", "env_code"),

    FIELD_USER_ID("userId", "user_Id"),
    /**
     * key
     */
    FIELD_TENANT_APP_KEY("tenantAppKey", "tenant_app_key"),
    ;

    private String fieldName;
    private String columnName;

}
