package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("压测报告指标比对")
public class ReportCompareTargetOut implements Serializable {

    @ApiModelProperty(value = "报告id")
    private Long reportId;

    @ApiModelProperty(value = "压测时长")
    private String pressureTestTime;

    @ApiModelProperty(value = "请求总数")
    private Long totalRequest;

    @ApiModelProperty(value = "并发数")
    private Integer concurrent;

    @ApiModelProperty(value = "平均TPS")
    private BigDecimal avgTps;

    @ApiModelProperty(value = "最大TPS")
    private BigDecimal maxTps;

    @ApiModelProperty(value = "最小TPS")
    private BigDecimal minTps;

    @ApiModelProperty(value = "成功率")
    private BigDecimal successRate;

    @ApiModelProperty(value = "sa")
    private BigDecimal sa;

    @ApiModelProperty(value = "压测开始时间")
    private String startTime;

    @ApiModelProperty(value = "压测结束时间")
    private String endTime;
}
