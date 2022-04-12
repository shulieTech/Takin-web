package io.shulie.takin.web.biz.pojo.request.report;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ReportPerformanceCostTrendRequest implements Serializable {

    private Long reportId;
    private Long sceneId;
    private String xpathMd5;
    private List<ServiceParam> services;

    @Data
    public static class ServiceParam implements Serializable {
        private String appName;
        private String serviceName;
        private String methodName;
        private String rpcType;
        private String entranceAppName;
        private String entranceServiceName;
        private String entranceMethodName;
        private String entranceRpcType;
    }

}
