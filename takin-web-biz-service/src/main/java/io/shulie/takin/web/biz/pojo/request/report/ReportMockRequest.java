package io.shulie.takin.web.biz.pojo.request.report;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据报告查询mock-trace记录
 */
@Data
public class ReportMockRequest implements Serializable {
    private String startTime;
    private String endTime;
    private Long reportId;
    private Long tenantId;
    private String envCode;
}

