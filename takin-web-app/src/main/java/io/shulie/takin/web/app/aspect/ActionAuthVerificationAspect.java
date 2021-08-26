package io.shulie.takin.web.app.aspect;

import java.util.Objects;

import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.ext.api.auth.WebOperationAuthExtApi;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 操作权限鉴权
 *
 * @author fanxx
 * @date 2020/11/7 下午9:09
 */
@Aspect
@Component
@Slf4j
@Order(0)
public class ActionAuthVerificationAspect {
    @Autowired
    private PluginManager pluginManager;

    /**
     * 指定切入点
     */
    @Pointcut("@annotation(io.shulie.takin.common.beans.annotation.AuthVerification)")
    private void controllerAspect() { /* no context */}

    /**
     * 方法开始执行
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint jp) throws TakinWebException {
        WebOperationAuthExtApi operationAuthApi = pluginManager.getExtension(WebOperationAuthExtApi.class);
        if (Objects.isNull(operationAuthApi)) {
            return;
        }
        operationAuthApi.validateActionAuth(jp);
    }
}
