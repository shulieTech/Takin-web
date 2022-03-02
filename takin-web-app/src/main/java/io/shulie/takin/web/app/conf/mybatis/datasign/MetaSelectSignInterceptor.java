package io.shulie.takin.web.app.conf.mybatis.datasign;

import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.annocation.EnableSign;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * @Author: 南风
 * @Date: 2022/2/23
 */
@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = Statement.class))
@Component
@AllArgsConstructor
@Slf4j
public class MetaSelectSignInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List result = (List) invocation.proceed();
        if (result.isEmpty()) return result;
        Class<?> clz = result.get(0).getClass();

        if (clz.isAnnotationPresent(EnableSign.class)) {
            log.info("【sign operation】select SQL 开始执行签名计算");
            boolean sign = SignCommonUtil.checkSignData(clz, result);

            if(!sign){
                log.error("【sign operation fail】select SQL 签名验证失败");
                return new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR,"select SQL 签名验证失败");
            }
            log.info("【sign operation】select SQL 签名验证成功");
            return result;
        }
        return result;

    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
