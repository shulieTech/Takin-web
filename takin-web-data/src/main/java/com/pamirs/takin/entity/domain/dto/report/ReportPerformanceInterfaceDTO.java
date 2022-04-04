package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReportPerformanceInterfaceDTO implements Serializable {

    private String reportId;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * rpcType
     */
    private String rpcType;
    /**
     * 入口应用名称
     */
    private String entranceAppName;
    /**
     * 入口服务名称
     */
    private String entranceServiceName;
    /**
     * 入口方法名称
     */
    private String entranceMethodName;
    /**
     * 入口rpcType
     */
    private String entranceRpcType;
    /**
     * 调用总次数
     */
    private Long reqCnt;
    /**
     * 平均自耗时
     */
    private BigDecimal avgCost;
    /**
     * 最大耗时
     */
    private Long maxCost;
    /**
     * 耗时占比
     */
    private BigDecimal costPercent;
}
