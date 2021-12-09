package io.shulie.takin.web.data.param.report;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.param.report
 * @ClassName: ReportApplicationSummaryCreateParam
 * @Description: TODO
 * @Date: 2021/11/23 11:03
 */
@Data
public class ReportApplicationSummaryCreateParam {
    private Long id;

    private Long reportId;

    private String applicationName;

    /**
     * 总机器数
     */
    private Integer machineTotalCount;

    /**
     * 风险机器数
     */
    private Integer machineRiskCount;
}
