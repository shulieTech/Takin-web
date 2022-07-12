package io.shulie.takin.web.ext.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @date 2021/8/2 21:07
 */
@Data
public class SsoUserExt {
    private Long id;

    private String name;

    private String nick;

    /**
     * 用户类型 0:系统管理员 1:普通用户 2.租户管理员
     */
    private Integer userType;

    @JsonProperty("xToken")
    private String xToken;

    /**
     * 环境code
     */
    private String envCode;

    /**
     * 租户code
     */
    private String tenantCode;

    /**
     * 是否有超级管理员的权限(创建租户)
     */
    private Integer isSuper;

    /**
     * 登录状态
     */
    private boolean loginStatus;
}
