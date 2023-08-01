package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportCostTrendQueryReq extends ContextExt {

    private Integer minCost;

    private Integer maxCost;

    private Long startTime;

    private Long endTime;

    private Long jobId;

    private String serviceName;

    private String requestMethod;

    private String transaction;

    /**
     * 租户标识
     */
    @ApiModelProperty("租户标识,系统自动赋值")
    private String tenantAppKey;
    /**
     * 环境标识
     */
    @ApiModelProperty("环境标识,系统自动赋值")
    private String envCode;
}
