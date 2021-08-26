package io.shulie.takin.web.ext.api.auth;

import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;

/**
 * 用户权限模块
 *
 * @author by: hezhongqi
 * @date 2021/8/7 12:45
 */
public interface WebUserAuthExtApi extends ExtensionPoint {
    /**
     * 验证是否是超级管理员
     *
     * @return -
     */
    boolean validateSuperAdmin();
}
