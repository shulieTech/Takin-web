package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("压测报告比对-柱状图")
public class ReportCompareColumnarOut implements Serializable {

    private BigDecimal tps;

    private BigDecimal rt;

    private BigDecimal successRate;

    private Long reportId;

    public BigDecimal getTps() {
        return tps != null ? tps.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public BigDecimal getRt() {
        return rt != null ? rt.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public BigDecimal getSuccessRate() {
        return successRate != null ? successRate.setScale(2, BigDecimal.ROUND_HALF_UP) : null;
    }

    public void setTps(BigDecimal tps) {
        this.tps = (tps == null ? null : tps.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void setRt(BigDecimal rt) {
        this.rt = (rt == null ? null : rt.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = (successRate == null ? null : successRate.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}
