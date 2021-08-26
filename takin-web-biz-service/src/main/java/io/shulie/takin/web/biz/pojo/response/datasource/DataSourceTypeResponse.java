package io.shulie.takin.web.biz.pojo.response.datasource;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/6 5:11 下午
 */
@Data
public class DataSourceTypeResponse {
    /**
     * 数据源类型值
     */
    private Integer value;

    /**
     * 数据源类型名称
     */
    private String label;
}
