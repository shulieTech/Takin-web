package io.shulie.takin.web.amdb.bean.result.report;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportInterfaceMetricsDTO extends ReportDTOBased {
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
     * 时间窗口
     */
    private Date timeWindow;
    /**
     * 时间窗口内采样后请求数
     */
    private Long countAfterSimp;

}
