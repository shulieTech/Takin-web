package io.shulie.takin.web.app.conf.mybatis.datasign;

import cn.hutool.core.lang.copier.Copier;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SignUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.google.common.base.Strings;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: 南风
 * @Date: 2022/2/23
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Component
@AllArgsConstructor
@Slf4j
public class MetaUpdateSignInterceptor implements Interceptor {

    public static String privateKey = "1545345";

    public static String SIGN_FIELD = "sign";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // 先判断是不是update操作 不是直接过滤
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (!SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        // 解析
        Map map = (Map) boundSql.getParameterObject();
        Object et = map.get(Constants.ENTITY);
        Class<?> clz = et.getClass();
        boolean isSign = clz.isAnnotationPresent(EnableSign.class);

        if (isSign) {
            log.info("【sign operation】update SQL 开始执行签名计算");
            //解析sql,拿出where条件，构建查询sql获取更新范围的数据,进行验签
            String sql = boundSql.getSql();

            Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
            TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
            Configuration configuration = mappedStatement.getConfiguration();

            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings != null) {
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value;
                        String propertyName = parameterMapping.getProperty();
                        if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            metaObject = configuration.newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }
                        sql = sql.replaceFirst("\\?", "'" + value + "'");

                    }
                }
            }
            System.out.println("------sql-----:"+sql);

            //计算签名
            String newSign = buildSign(et, clz);
            //替换新签名
            Field sign = clz.getDeclaredField(SIGN_FIELD);
            sign.setAccessible(true);
            sign.set(et, newSign);

            return invocation.proceed();
        }
        return invocation.proceed();

    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }


    private String buildSign(Object et, Class<?> clz) throws IllegalAccessException {
        Map<String, Integer> keySortMap = new HashMap<>();
        Map<String, String> signMap = new HashMap<>();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            if (field.isAnnotationPresent(SignField.class)) {
                SignField signField = field.getAnnotation(SignField.class);
                keySortMap.put(field.getName(), signField.order());
                signMap.put(field.getName(), field.get(et).toString());

            }

        }
        keySortMap = MapUtil.sortByValue(keySortMap, false);
        Map<String, String> signMapSort = new HashMap<>();
        for (String param : keySortMap.keySet()) {
            signMapSort.put(param, signMap.get(param));
        }

        return  SignUtil.signParamsMd5(signMapSort, privateKey);
    }
}
