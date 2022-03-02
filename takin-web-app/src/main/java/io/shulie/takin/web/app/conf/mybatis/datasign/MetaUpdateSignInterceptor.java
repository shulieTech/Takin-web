package io.shulie.takin.web.app.conf.mybatis.datasign;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SignUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.annocation.EnableSign;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;
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
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @Author: 南风
 * @Date: 2022/2/23
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Component
@Slf4j
public class MetaUpdateSignInterceptor implements Interceptor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public MetaUpdateSignInterceptor() {
    }

    public MetaUpdateSignInterceptor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


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
                        if (boundSql.hasAdditionalParameter(propertyName)) {
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            metaObject = configuration.newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }

                        //类型处理
                        if (value instanceof LocalDateTime) {
                            value = DateUtil.format((LocalDateTime) value, DatePattern.NORM_DATETIME_PATTERN);
                        } else if (value instanceof Date) {
                            value = DateUtil.format((Date) value, DatePattern.NORM_DATETIME_PATTERN);
                        } else if (value instanceof Boolean) {
                            value = Boolean.FALSE.equals(value) ? "0" : "1";
                        }
                        sql = sql.replaceFirst("\\?", "'" + value + "'");
                    }
                }
            }

            Update update = (Update) CCJSqlParserUtil.parse(boundSql.getSql());
            String tableName = update.getTable().getName();
            String whereStr = " where" + sql.split("WHERE")[1];

            String querySql = "select * from " + tableName + whereStr;

            //开始校验签名
            List<?> result = jdbcTemplate.query(querySql, new BeanPropertyRowMapper<>(clz));

            if (result.isEmpty()) {
                log.error("【sign operation fail】update SQL 签名验证失败,查不到需要更新的数据");
                return new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "update SQL 签名验证失败");
            }

            boolean sign = SignCommonUtil.checkSignData(clz, result);

            if (!sign) {
                log.error("【sign operation fail】update before select SQL 签名验证失败");
                return new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "update SQL 签名验证失败");
            }
            log.info("【sign operation】update before select SQL 签名验证通过");

            //计算签名
            String newSign = buildSign(et, clz);
            //替换新签名
            Field field = clz.getDeclaredField(SIGN_FIELD);
            field.setAccessible(true);
            field.set(et, newSign);

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

        List<Field> signFields = SignCommonUtil.getSignFields(clz);
        Map<String, String> signMap = new LinkedHashMap<>();
        for (Field field : signFields) {
            if(SIGN_FIELD.equals(field.getName())){
                continue;
            }
            signMap.put(field.getName(), field.get(et).toString());
        }
        return SignUtil.signParamsMd5(signMap, privateKey);
    }

}
