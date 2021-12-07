package io.shulie.takin.web.data.param.report;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.param.report
 * @ClassName: ReportSummaryCreateParam
 * @Description: TODO
 * @Date: 2021/11/23 09:57
 */
@Data
public class ReportSummaryCreateParam {
    private Long reportId;

    /**
     * 瓶颈接口
     */
    private Integer bottleneckInterfaceCount;

    /**
     * 风险机器数
     */
    private Integer riskMachineCount;

    /**
     * 业务活动数
     */
    private Integer businessActivityCount;

    /**
     * 未达标业务活动数
     */
    private Integer unachieveBusinessActivityCount;

    /**
     * 应用数
     */
    private Integer applicationCount;

    /**
     * 机器数
     */
    private Integer machineCount;

    /**
     * 告警次数
     */
    private Integer warnCount;
}
