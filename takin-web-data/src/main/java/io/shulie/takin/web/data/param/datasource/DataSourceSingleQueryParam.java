package io.shulie.takin.web.data.param.datasource;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/30 11:29 上午
 */
@Data
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
