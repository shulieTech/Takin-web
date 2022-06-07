package io.shulie.takin.web.app.conf.mybatis;

/**
 * @Author: 南风
 * @Date: 2022/5/20 2:11 下午
 */

import io.shulie.takin.web.app.conf.cache.TenantSignCacheManage;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Properties;

@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})

})
public class TenantSignContextInterceptor implements Interceptor, ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Long tenantId = WebPluginUtils.traceTenantId();
        TenantSignCacheManage cacheManage = applicationContext.getBean(TenantSignCacheManage.class);
        cacheManage.setContext(tenantId);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
