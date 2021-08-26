package io.shulie.takin.web.data.result.leakcheck;

import java.util.Date;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:35 下午
 */
@Data
public class LeakCheckConfigResult {
    private Long id;

    /**
     * 业务活动id
     */
    private Long businessActivityId;

    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 漏数sql主键集合，逗号分隔
     */
    private String leakcheckSqlIds;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否有效 0:有效;1:无效
     */
    private Boolean isDeleted;
}
