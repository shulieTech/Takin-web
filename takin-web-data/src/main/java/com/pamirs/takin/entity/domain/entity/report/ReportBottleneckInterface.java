package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReportBottleneckInterface {

    private Long id;

    private Long reportId;

    private String applicationName;

    private Integer sortNo;

    private String interfaceType;

    private String interfaceName;

    private BigDecimal tps;

    private BigDecimal rt;

    private Integer errorReqs;

    private Integer nodeCount;

    private BigDecimal bottleneckWeight;

}
