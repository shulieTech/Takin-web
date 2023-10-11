package io.shulie.takin.web.ext.entity.traffic;

import lombok.Data;

@Data
public class TrafficRecorderExtResponse {

    private String traceId;
    private String appName;
    private String serviceName;
    private String methodName;
    private String requestHeader;
    private String requestBody;
    private Long startTime;
    private String requestParam;

}
