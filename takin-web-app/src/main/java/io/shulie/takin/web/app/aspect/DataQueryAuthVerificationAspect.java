package io.shulie.takin.web.app.aspect;

import java.util.Objects;

import io.shulie.takin.plugin.framework.core.PluginManager;
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
 * 用户数据权限初始化
 *
 * @author fanxx
 * @date 2020/11/9 10:04 上午
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class DataQueryAuthVerificationAspect {

    @Autowired
    private PluginManager pluginManager;

    /**
     * 切入点
     */
    @Pointcut("@annotation(io.shulie.takin.common.beans.annotation.AuthVerification)")
    private void controllerAspect() { /* no context */}

    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        WebOperationAuthExtApi operationAuthApi = pluginManager.getExtension(WebOperationAuthExtApi.class);
        if (Objects.isNull(operationAuthApi)) {
            return;
        }
        operationAuthApi.setDataAuthContext(joinPoint);
    }
}
