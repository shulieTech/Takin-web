package io.shulie.takin.web.data.param.report;

import java.math.BigDecimal;

import lombok.Data;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/11/23 10:19
 */
@Data
public class ReportMachineUpdateParam {

    private Long id;

    private Long reportId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 机器ip
     */
    private String machineIp;

    /**
     * 机器ip
     */
    private String agentId;

    /**
     * 机器基本信息
     */
    private String machineBaseConfig;

    /**
     * 机器tps对应指标信息
     */
    private String machineTpsTargetConfig;

    /**
     * 风险计算值
     */
    private BigDecimal riskValue;

    /**
     * 是否风险机器(0-否，1-是)
     */
    private Integer riskFlag;

    /**
     * 风险提示内容
     */
    private String riskContent;
}
