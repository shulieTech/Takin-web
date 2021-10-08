package io.shulie.takin.web.biz.service.report;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author qianshui
 * @date 2020/7/28 上午10:59
 */
public interface ReportTaskService {

    /**
     * 根据租户获取报告id 列表
     * @param ext
     * @return
     */
    List<Long> getRunningReport(TenantCommonExt ext);

    /**
     * 完成报告
     */
    void finishReport(Long reportId);

    /**
     * 同步应用基础信息
     */
    void syncMachineData(Long reportId);

    /**
     * tps指标图
     */
    void calcTpsTarget(Long reportId);

    /**
     * 汇总应用 机器数 风险机器数
     */
    void calcApplicationSummary(Long reportId);

}
