package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ReportPerformanceCostTrendDTO implements Serializable {

    private List<String> time;
    private List<ServiceMetrics> list;

    @Data
    @EqualsAndHashCode(exclude = "data")
    public static class ServiceMetrics implements Serializable {
        private String entranceAppName;
        private String entranceServiceName;
        private String entranceMethodName;
        private String entranceRpcType;
        private String appName;
        private String serviceName;
        private String methodName;
        private String rpcType;
        private List<String> data;

    }
}
