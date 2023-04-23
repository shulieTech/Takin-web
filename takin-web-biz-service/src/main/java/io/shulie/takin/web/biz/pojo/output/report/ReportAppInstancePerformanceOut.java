package io.shulie.takin.web.biz.pojo.output.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ReportAppInstancePerformanceOut", description = "应用实例性能指标")
public class ReportAppInstancePerformanceOut {
    @ApiModelProperty(value = "应用名称")
    private String appName;
    @ApiModelProperty(value = "实例名称")
    private String instanceName;
    @ApiModelProperty(value = "平均CPU使用率")
    private BigDecimal avgCpuUsageRate;

    @ApiModelProperty(value = "平均内存使用率")
    private BigDecimal avgMemUsageRate;
    @ApiModelProperty(value = "平均磁盘IO使用率")
    private BigDecimal avgDiskIoWaitRate;
    @ApiModelProperty(value = "平均网络使用率")
    private BigDecimal avgNetUsageRate;
    @ApiModelProperty(value = "GC次数")
    private int gcCount;
    @ApiModelProperty(value = "GC时间")
    private BigDecimal gcCost;
    @ApiModelProperty(value = "平均TPS")
    private BigDecimal avgTps;

}
