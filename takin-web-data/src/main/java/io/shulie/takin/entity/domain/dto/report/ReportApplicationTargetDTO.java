package io.shulie.takin.entity.domain.dto.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ReportApplicationTargetDTO implements Serializable {

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("实例ID")
    private String agentId;

    @ApiModelProperty("CPU")
    private BigDecimal cpu;

    private BigDecimal load;

    private BigDecimal memory;

    private BigDecimal io;

    @ApiModelProperty("带宽")
    private BigDecimal mbps;

    private Long fullGcCount;

    private Integer fullGcCost;

    private Long youngGcCount;

    private Integer youngGcCost;


}
