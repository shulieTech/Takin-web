package io.shulie.takin.web.app.aspect;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author qianshui
 * @date 2020/5/14 下午8:47
 */
@Aspect
@Component
@Slf4j
public class CloudApiAspect {

    @Pointcut("execution(public * io.shulie.takin.web.diff.cloud.impl..*.*(..))")
    public void myAspect() {

    }

    @Before("myAspect()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        if (params != null && params.length == 1) {
            if (params[0] instanceof ContextExt) {
                ContextExt inParam = (ContextExt)params[0];
                WebPluginUtils.fillCloudUserData(inParam);
            }
        }
    }

    @Pointcut("execution(public * io.shulie.takin.cloud.sdk.impl..*.*(..))")
    public void sdk() {

    }

    @Before("sdk()")
    public void beforeSdk(JoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        if (params != null && params.length == 1) {
            if (params[0] instanceof ContextExt) {
                ContextExt inParam = (ContextExt)params[0];
                WebPluginUtils.fillCloudUserData(inParam);
            }
        }
    }
}
