package io.shulie.takin.web.app.aspect;

import java.util.Objects;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

import io.shulie.takin.web.ext.api.user.WebUserExtApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.plugin.framework.core.PluginManager;

/**
 * @author qianshui
 * @date 2020/5/14 下午8:47
 */
@Slf4j
@Aspect
@Component
public class LicenseAspect {

    @Resource
    private PluginManager pluginManager;

    @Pointcut("execution(public * io.shulie.takin.cloud.sdk.impl..*.*(..))")
    public void setLicense() {

    }

    @Before("setLicense()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        if (params != null && params.length == 1) {
            if (params[0] instanceof ContextExt) {
                ContextExt inParam = (ContextExt)params[0];
                WebUserExtApi userExtApi = pluginManager.getExtension(WebUserExtApi.class);
                if (!Objects.isNull(userExtApi)) {userExtApi.fillCloudUserData(inParam);}
            }
        }
    }
}
