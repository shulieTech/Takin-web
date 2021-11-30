package io.shulie.takin.web.data.param.report;

import lombok.Data;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/11/23 11:03
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
