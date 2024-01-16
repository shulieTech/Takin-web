package io.shulie.takin.web.data.param.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportMockCreateParam implements Serializable {
    private Long reportId;
    private String startTime;
    private String endTime;
    private String appName;
    private String mockName;
    private String middlewareName;
    private String mockType;
    private String mockScript;
    private String mockStatus;
    private Long failureCount;
    private Long successCount;
    private Double avgRt;
    private Long tenantId;
    private String envCode;
}
