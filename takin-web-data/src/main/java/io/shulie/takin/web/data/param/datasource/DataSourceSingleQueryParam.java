package io.shulie.takin.web.data.param.datasource;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.UserCommonExt;

/**
 * @author fanxx
 * @date 2020/12/30 11:29 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataSourceSingleQueryParam extends UserCommonExt {
    /**
     * 数据源id
     */
    private Long id;
    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源地址
     */
    private String jdbcUrl;


}
