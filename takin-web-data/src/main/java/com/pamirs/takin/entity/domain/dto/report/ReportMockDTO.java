package com.pamirs.takin.entity.domain.dto.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportMockDTO implements Serializable {
    private Long id;
    private Long reportId;
    private String startTime;
    private String endTime;
    private String appName;
    private String mockName;
    private String mockType;
    private String mockScript;
    private String mockStatus;
    private Long failureCount;
    private Long successCount;
    private Double avgRt;
}
