package io.shulie.takin.web.biz.pojo.request.report;

import lombok.Data;

@Data
public class ReportMockResponse {

    private String appName;
    private String serviceName;
    private String methodName;
    private Long failureCount;
    private Long successCount;
    private Double avgRt;

}
