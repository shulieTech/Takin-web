package io.shulie.takin.web.data.param.datasource;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/30 10:16 上午
 */
@Data
public class DataSourceQueryParam extends PagingDevice {
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
     * 数据源id集合
     */
    private List<Long> dataSourceIdList;

    /**
     * 部门id
     */
    private Long deptId;
}
