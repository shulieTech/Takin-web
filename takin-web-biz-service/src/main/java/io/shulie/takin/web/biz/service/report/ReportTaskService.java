package io.shulie.takin.web.biz.service.report;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author qianshui
 * @date 2020/7/28 上午10:59
 */
public interface ReportTaskService {

    /**
     * 完成报告
     * @param reportId
     * @return 报告是否正常结束 true=是 false=否
     */
    Boolean finishReport(Long reportId, TenantCommonExt commonExt);

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
