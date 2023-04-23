package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "ReportAppPerformanceOut", description = "应用性能指标")
public class ReportAppPerformanceOut {
    @ApiModelProperty(value = "应用名称")
    private String appName;
    @ApiModelProperty(value = "总请求数")
    private BigDecimal totalRequest;
    @ApiModelProperty(value = "平均TPS")
    private BigDecimal avgTps;

    @ApiModelProperty(value = "平均RT")
    private BigDecimal avgRt;

    @ApiModelProperty(value = "最大RT")
    private BigDecimal maxRt;

    @ApiModelProperty(value = "最小RT")
    private BigDecimal minRt;

    @ApiModelProperty(value = "成功率")
    private BigDecimal successRate;

    @ApiModelProperty(value = "sa")
    private BigDecimal sa;

    @ApiModelProperty(value = "压测时间")
    private Date startTime;

}
