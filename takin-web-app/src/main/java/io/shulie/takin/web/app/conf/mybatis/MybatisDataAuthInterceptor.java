package io.shulie.takin.web.app.conf.mybatis;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;
import io.shulie.takin.web.common.annocation.DataAuth;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

/**
 * 数据权限拦截器<br/>
 * 根据各个微服务,继承DataAuthService增加不同的where语句
 *
 * @author qianshui
 * @date 2020/11/5 下午4:46
 */
@Component
@Intercepts({
    @Signature(method = "query", type = Executor.class,
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class MybatisDataAuthInterceptor implements Interceptor {


    private static final String BOUND_SQL = "sqlSource.boundSql.sql";

    @Override
    public Object intercept(Invocation arg0) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement)arg0.getArgs()[0];
        //如果没有用户拓展插件，则无需过滤数据权限
        if (!WebPluginUtils.checkUserPlugin()) {
            return arg0.proceed();
        }
        //仅拦截select 查询
        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return arg0.proceed();
        }
        //Allow All 系统管理员 租户管理员
        if (allowAll()) {
            return arg0.proceed();
        }
        // 所有sql 都进行拦截
        BoundSql boundSql = mappedStatement.getBoundSql(arg0.getArgs()[1]);
        Select select = (Select)CCJSqlParserUtil.parse(boundSql.getSql());
        PlainSelect plainSelect = (PlainSelect)select.getSelectBody();
        //增加用户拦截 in表达式
        InExpression inExpression = new InExpression();
        ExpressionList expressionList = new ExpressionList();
        //从本地线程获取 数据权限
        expressionList.setExpressions(getExpressionList(mappedStatement.getId()));
        inExpression.setLeftExpression(new Column(new Table(action.tableAlias()), AUTH_COLUMN));
        inExpression.setRightItemsList(expressionList);
        //原来的where + 本次的in
        plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), inExpression));
        //create new MappedStatement
        MappedStatement newMappedStatement = newMappedStatement(mappedStatement, new BoundSqlSqlSource(boundSql));
        MetaObject metaObject = MetaObject.forObject(newMappedStatement,
            new DefaultObjectFactory(),
            new DefaultObjectWrapperFactory(),
            new DefaultReflectorFactory());
        metaObject.setValue(BOUND_SQL, plainSelect.toString());
        arg0.getArgs()[0] = newMappedStatement;
        return arg0.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties arg0) {

    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
            ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if ((ms.getKeyProperties() != null) && (ms.getKeyProperties().length != 0)) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    private List<Expression> getExpressionList(String mappedStatementId) {
        List<Long> allowUserIdList = WebPluginUtils.getQueryAllowUserIdList();
        if (CollectionUtils.isEmpty(allowUserIdList)) {
            //log.warn("RestContext AllowUserIdList Is Empty.... userId={}, mappedStatementId={}", CustomUtil
            // .getUserId(),
            //    mappedStatementId);
            return Lists.newArrayList(new LongValue(-1));
        }
        List<Expression> expressionList = Lists.newArrayList();
        allowUserIdList.stream().forEach(data -> expressionList.add(new LongValue(data)));
        return expressionList;
    }

    private Boolean allowAll() {
        List<Long> allowUserIdList = WebPluginUtils.getQueryAllowUserIdList();
        // 如果是空的 则查所有
        if (CollectionUtils.isEmpty(allowUserIdList)) {
            return true;
        }
        return false;
    }

    class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return this.boundSql;
        }
    }
}
