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

    public BigDecimal getAvgTps() {
        return avgTps != null ? avgTps.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public BigDecimal getMaxTps() {
        return maxTps != null ? maxTps.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public BigDecimal getMinTps() {
        return minTps != null ? minTps.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public BigDecimal getSuccessRate() {
        return successRate != null ? successRate.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public BigDecimal getSa() {
        return sa != null ? sa.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public void setAvgTps(BigDecimal avgTps) {
        this.avgTps = (avgTps == null ? null : avgTps.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void setMaxTps(BigDecimal maxTps) {
        this.maxTps = (maxTps == null ? null : maxTps.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void setMinTps(BigDecimal minTps) {
        this.minTps = (minTps == null ? null : minTps.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = (successRate == null ? null : successRate.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void setSa(BigDecimal sa) {
        this.sa = (sa == null ? null : sa.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}
