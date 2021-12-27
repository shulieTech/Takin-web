package io.shulie.takin.web.amdb.bean.query.application;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/16 10:37 上午
 * @Description:
 */
@Data
@Builder
public class BatchNodeMetricsQueryDTO {
    /**
     * 租户ID
     */
    private String tenantAppKey;

    /**
     * 环境编码
     */
    private String envCode;

    /**
     * 起始时间
     */
    private Long startTime;

    /**
     * 截止时间
     */
    private Long endTime;

    /**
     * 压测流量标识(1为压测流量,0为业务流量,混合则为-1)
     */
    private Integer clusterTest;

    /**
     * 调用来源 tro/e2e
     */
    private String querySource;

    /**
     * 边集合
     */
    private List<String> eagleIds;

}
