package io.shulie.takin.web.data.param.datasource;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/30 9:58 上午
 */
@Data
public class DataSourceCreateParam {
    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源地址
     */
    private String jdbcUrl;

    /**
     * 数据源用户
     */
    private String username;

    /**
     * 数据源密码
     */
    private String password;

    /**
     * 部门id
     */
    private Long deptId;
}
