package com.pamirs.takin.entity.domain.entity.report;

import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
public class ReportApplicationSummary extends TenantBaseEntity {

    private Long id;

    private Long reportId;

    private String applicationName;

    private Integer machineTotalCount;

    private Integer machineRiskCount;

}