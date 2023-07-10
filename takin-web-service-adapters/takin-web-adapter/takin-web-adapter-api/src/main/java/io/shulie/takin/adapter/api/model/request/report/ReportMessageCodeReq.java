package io.shulie.takin.adapter.api.model.request.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReportMessageCodeReq implements Serializable {

    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;

    @ApiModelProperty(value = "压测引擎任务ID", required = true)
    private Long jobId;

    @ApiModelProperty(value = "服务", required = true)
    private String serviceName;

    @ApiModelProperty("请求方式")
    private String requestMethod;

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
