package io.shulie.takin.web.biz.pojo.output.report;

import lombok.Data;

/**
 * @author zhangz
 * Created on 2023/11/28 16:53
 * Email: zz052831@163.com
 */

@Data
public class ReportRiskItemOutput {
    /**
     * 风险项ID
     */
    private String riskItemId;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 服务信息
     */
    private String targetUid;
    /**
     * 风险名称
     */
    private String riskName;
    /**
     * 风险利润
     */
    private String profit;
    /**
     * 单次风险利润
     */
    private String singleProfit;
    /**
     * 修复成本
     */
    private String repairCost;
    /**
     * 原因个数
     */
    private String causeCount;
    /**
     * 影响链路数
     */
    private String influenceChainCount;

    private Integer ranking;
}
