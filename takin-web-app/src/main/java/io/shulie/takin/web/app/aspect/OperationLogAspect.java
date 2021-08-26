package io.shulie.takin.web.app.aspect;

import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.ext.api.log.WebOperationLogExtApi;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-15
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private PluginManager pluginManager;

    /**
     * 指定切入点
     */
    @Pointcut("@annotation(io.shulie.takin.common.beans.annotation.ModuleDef)")
    private void controllerAspect() { /* no context */}

    /**
     * 方法开始执行
     */
    @Before("controllerAspect()")
    public void doBefore() {
        OperationLogContextHolder.reset();
        OperationLogContextHolder.start();
    }

    /**
     * 方法结束执行后的操作
     */
    @AfterReturning("controllerAspect()")
    public void doAfter(JoinPoint jp) {
        OperationLogContextHolder.end(true);
        record(jp);
    }

    /**
     * 方法有异常时的操作
     */
    @AfterThrowing("controllerAspect()")
    public void doAfterThrow(JoinPoint jp) {
        OperationLogContextHolder.end(false);
        //        record(jp);
    }

    /**
     * 记录日志
     *
     * @param jp 切点
     */
    private void record(JoinPoint jp) {
        WebOperationLogExtApi logExtApi = pluginManager.getExtension(WebOperationLogExtApi.class);
        if (logExtApi != null) {
            logExtApi.recordLog(jp);
        }
    }
}
