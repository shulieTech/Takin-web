package io.shulie.takin.web.ext.entity.tenant;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: TenantInfoExt
 * @Description: 租户信息类
 * @Date: 2021/9/27 09:59
 */
@Data
public class TenantInfoExt {
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 租户名
     */
    private String tenantName;

    /**
     * 租户名
     */
    private String tenantNick;
    /**
     * 租户代码
     */
    private String tenantCode;
    /**
     * 租户key
     */
    private String userAppKey;
    /**
     * 租户环境
     */
    private List<TenantEnv> envs;

    @Data
    @NoArgsConstructor
    public class TenantEnv {
        /**
         * 环境代码
         */
        private String envCode;
        /**
         * 环境名
         */
        private String envName;
    }
}
