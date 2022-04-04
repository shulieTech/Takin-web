package io.shulie.takin.web.data.param.report;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ReportInterfaceMetricsQueryParam implements Serializable {

    private Long reportId;
    private List<MetricsServiceParam> services;

    @Data
    public static class MetricsServiceParam implements Serializable {
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
