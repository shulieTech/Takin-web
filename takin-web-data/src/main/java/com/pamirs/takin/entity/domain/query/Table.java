package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

/**
 * 说明:
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/9/8 16:48
 */
public class Table implements Serializable {

    //序列号
    private static final long serialVersionUID = 5022541880836487879L;

    /**
     * sql语句
     */
    private String sql;

    /**
     * 表名
     */
    private String tableName;

    /**
     * Gets the value of sql.
     *
     * @return the value of sql
     * @author shulie
     * @version 1.0
     */
    public String getSql() {
        return sql;
    }

    /**
     * Sets the sql.
     *
     * <p>You can use getSql() to get the value of sql</p>
     *
     * @param sql sql
     * @author shulie
     * @version 1.0
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Gets the value of tableName.
     *
     * @return the value of tableName
     * @author shulie
     * @version 1.0
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the tableName.
     *
     * <p>You can use getTableName() to get the value of tableName</p>
     *
     * @param tableName tableName
     * @author shulie
     * @version 1.0
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "Table{" +
            "sql='" + sql + '\'' +
            ", tableName='" + tableName + '\'' +
            '}';
    }
}
