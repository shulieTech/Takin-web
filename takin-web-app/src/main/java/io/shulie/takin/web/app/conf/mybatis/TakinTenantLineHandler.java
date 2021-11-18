package io.shulie.takin.web.app.conf.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.app.conf.mybatis
 * @ClassName: MyTenantLineHandler
 * @Description: 自定义租户插件
 * @Date: 2021/10/18 18:06
 */
public interface TakinTenantLineHandler extends TenantLineHandler {

    /**
     * 环境 code
     * @return
     */
    Expression getEnvCode();

    /**
     * 默认 字段名
     * @return
     */
    default String getEnvCodeColumn() {
        return TenantField.FIELD_ENV_CODE.getColumnName();
    }


    ///**
    // * 用户id
    // * @return
    // */
    //Expression getUserId();
    //
    ///**
    // * 用户字段
    // * @return
    // */
    //default String getUserIdColumn() {
    //    return TenantField.FIELD_USER_ID.getColumnName();
    //}

    /**
     * 已带查询条件 则不添加
     * @param expression
     * @param columnName
     * @return
     */
    default boolean ignoreSearch(Expression expression, String columnName) {
        if (expression == null ) {
            // 都要增加过滤 提交
            return false;
        }
        // 判断是否已经带了
        return expression.toString().toLowerCase().contains(columnName.toLowerCase());
    }

}
