package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReportMachine {

    private Long id;

    private Long reportId;

    private String applicationName;

    private String machineIp;

    private String machineBaseConfig;

    private BigDecimal riskValue;

    private Integer riskFlag;

    private String riskContent;

    private String machineTpsTargetConfig;

    private String agentId;

}
