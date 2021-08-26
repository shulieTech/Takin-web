package io.shulie.takin.web.data.param.datasource;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/30 10:16 上午
 */
@Data
public class DataSourceUpdateParam {

    /**
     * 数据源id
     */
    private Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 数据源地址
     */
    private String jdbcUrl;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
