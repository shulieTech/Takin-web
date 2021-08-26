package io.shulie.takin.web.ext.api.auth;

import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;
import org.aspectj.lang.JoinPoint;

/**
 * @author fanxx
 * @date 2021/7/30 5:07 下午
 */
public interface WebOperationAuthExtApi extends ExtensionPoint {
    /**
     * 操作权限鉴权
     */
    void validateActionAuth(JoinPoint jp);

    /**
     * 设置用户数据权限上下文
     * @param jp
     */
    void setDataAuthContext(JoinPoint jp);
}
