package io.shulie.takin.web.biz.service.report;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/7/28 上午10:59
 */
public interface ReportTaskService {

    /**
     * 根据租户获取报告id 列表
     * @return
     */
    List<Long> getRunningReport();

    /**
     * 完成报告
     * @param reportId
     */
    void finishReport(Long reportId);

    /**
     * 同步应用基础信息
     * @param reportId
     */
    void syncMachineData(Long reportId);

    /**
     * tps指标图
     * @param reportId
     */
    void calcTpsTarget(Long reportId);

    /**
     * 汇总应用 机器数 风险机器数
     * @param reportId
     */
    void calcApplicationSummary(Long reportId);

}
