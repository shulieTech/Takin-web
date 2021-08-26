package io.shulie.takin.web.data.result.datasource;

import java.util.Date;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/29 4:36 下午
 */
@Data
public class DataSourceTagRefResult {
    private Long id;

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    private Date gmtCreate;

    private Date gmtUpdate;
}
