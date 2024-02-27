package io.shulie.takin.web.amdb.bean.result.trace;

import lombok.Data;

import java.io.Serializable;

@Data
public class TraceMockDTO implements Serializable {
    private String traceId;
    private String appName;
    private String serviceName;
    private String methodName;
    private String resultCode;
    private Long count;
    private Double totalCost;
}
