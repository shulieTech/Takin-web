package io.shulie.takin.web.amdb.bean.result.report;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportActivityInterfaceDTO extends ReportDTOBased {
    /**
     * 入口应用名称
     */
    private String entranceAppName;
    /**
     * 入口接口名称
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
     * 业务活动平均耗时
     */
    private BigDecimal serviceAvgCost;
    /**
     * 耗时占比
     */
    private BigDecimal costPercent;
}
