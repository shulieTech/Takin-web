package com.pamirs.takin.entity.domain.entity.report;

import lombok.Data;

@Data
public class ReportSummary {

    private Long id;

    private Long reportId;

    private Integer bottleneckInterfaceCount;

    private Integer riskMachineCount;

    private Integer businessActivityCount;

    private Integer unachieveBusinessActivityCount;

    private Integer applicationCount;

    private Integer machineCount;

    private Integer warnCount;

}