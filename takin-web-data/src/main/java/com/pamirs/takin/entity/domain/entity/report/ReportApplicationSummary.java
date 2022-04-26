package com.pamirs.takin.entity.domain.entity.report;

import lombok.Data;

@Data
public class ReportApplicationSummary {

    private Long id;

    private Long reportId;

    private String applicationName;

    private Integer machineTotalCount;

    private Integer machineRiskCount;

}