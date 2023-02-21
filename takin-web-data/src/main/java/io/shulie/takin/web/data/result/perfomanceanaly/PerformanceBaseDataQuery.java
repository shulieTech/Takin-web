package io.shulie.takin.web.data.result.perfomanceanaly;

import lombok.Data;

@Data
public class PerformanceBaseDataQuery {
    private String agentId;

    private String appName;

    private String appIp;

    private Long startTime;

    private Long endTime;

    private Long tenantId;

    private Integer limit;

    /**
     * 租户标识
     */
    private String tenantAppKey;
    /**
     * 环境标识
     */
    private String envCode;
}
