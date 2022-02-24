package io.shulie.takin.web.app.conf.mybatis.datasign;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SignUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
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
            //计算签名
            Map<String, String> signMap = buildSign(et, clz);
            Update update = (Update) CCJSqlParserUtil.parse(boundSql.getSql());
            //替换新签名
            Field pluginVersion = clz.getDeclaredField(SIGN_FIELD);
            pluginVersion.setAccessible(true);
            pluginVersion.set(et, signMap.get("newSign"));

            // 拼接签名查询条件
            Expression where = update.getWhere();
            EqualsTo signCondition = new EqualsTo();
            signCondition.setLeftExpression(new Column(SIGN_FIELD));
            signCondition.setRightExpression(new StringValue(signMap.get("oldSign")));
            AndExpression andExpression = new AndExpression(signCondition, where);
            update.setWhere(andExpression);

            log.debug("【sign operation】组装后的sql【{}】 ", update);
            metaObject.setValue("delegate.boundSql.sql", update.toString());
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


    private Map<String, String> buildSign(Object et, Class<?> clz) throws IllegalAccessException {
        String oldSign = "";
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

            if (SIGN_FIELD.equals(field.getName())) {
                oldSign = field.get(et).toString();
            }
        }
        keySortMap = MapUtil.sortByValue(keySortMap, false);
        Map<String, String> signMapSort = new HashMap<>();
        for (String param : keySortMap.keySet()) {
            signMapSort.put(param, signMap.get(param));
        }
        Map<String, String> map = new HashMap<>();
        map.put("newSign", SignUtil.signParamsMd5(signMapSort, privateKey));
        map.put("oldSign", oldSign);

        return map;
    }
}
