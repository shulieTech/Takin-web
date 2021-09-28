package io.shulie.takin.web.ext.api.log;

import org.aspectj.lang.JoinPoint;
import org.pf4j.ExtensionPoint;

/**
 * @author: 肖建璋
 * @date 2021/08/02/8:29 下午
 * 日志插件
 */
public interface WebOperationLogExtApi extends ExtensionPoint {
    /**
     * 添加日志x
     *
     * @param joinPoint 切面信息
     * @return OperationLogExt
     */
    void recordLog(JoinPoint joinPoint);

}
