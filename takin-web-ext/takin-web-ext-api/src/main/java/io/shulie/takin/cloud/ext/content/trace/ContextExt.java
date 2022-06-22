package io.shulie.takin.cloud.ext.content.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 溯源上下文
 *
 * @author 张天赐
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextExt {
    /**
     * 用户主键
     */
    Long userId;
    /**
     * 租户主键
     */
    Long tenantId;
    /**
     * 环境编码
     */
    String envCode;
    /**
     * SQL过滤标识
     */
    String filterSql;

    /**
     * 操作人名称
     */
    String userName;
    /**
     * 租户code
     */
    String tenantCode;

    String userAppKey;

    public String getFilterSql() {
        if (StringUtils.isBlank(filterSql)) {
            return null;
        }
        return filterSql;
    }

    /**
     * 清除上下文信息
     */
    public void clean() {
        this.setUserId(null);
        this.setTenantId(null);
        this.setEnvCode(null);
        this.setFilterSql(null);
        this.setUserName(null);
        this.setTenantCode(null);
        this.setUserAppKey(null);
    }
}