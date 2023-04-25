package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("压测报告RT比对")
public class ReportCompareRtOutput implements Serializable {
    @ApiModelProperty(value = "报告id")
    private Long reportId;
    @ApiModelProperty(value = "压测时长")
    private String pressureTestTime;
    @ApiModelProperty(value = "平均Rt")
    private BigDecimal avgRt;
    @ApiModelProperty(value = "最大Rt")
    private BigDecimal maxRt;
    @ApiModelProperty(value = "最小Rt")
    private BigDecimal minRt;
    @ApiModelProperty(value = "50分位Rt")
    private BigDecimal rt50;
    @ApiModelProperty(value = "75分位Rt")
    private BigDecimal rt75;
    @ApiModelProperty(value = "90分位Rt")
    private BigDecimal rt90;
    @ApiModelProperty(value = "95分位Rt")
    private BigDecimal rt95;
    @ApiModelProperty(value = "99分位Rt")
    private BigDecimal rt99;

    @ApiModelProperty(value = "压测开始时间")
    private String startTime;

    @ApiModelProperty(value = "压测结束时间")
    private String endTime;

}
