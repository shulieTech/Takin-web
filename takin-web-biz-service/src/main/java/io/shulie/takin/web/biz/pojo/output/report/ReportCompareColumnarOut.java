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
}
