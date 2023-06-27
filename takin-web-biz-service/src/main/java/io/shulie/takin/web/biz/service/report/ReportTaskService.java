package io.shulie.takin.web.biz.service.report;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/7/28 上午10:59
 */
public interface ReportTaskService {

    /**
     * 完成报告
     *
     * @param reportId
     * @return 报告是否正常结束 true=是 false=否
     */
    Boolean finishReport(Long reportId, TenantCommonExt commonExt);
    /**
     * 汇总实况数据，包括应用基础信息、tps指标图、应用机器数和风险机器
     *
     * @param reportId
     */
    void calcTmpReportData(Long reportId);

    List<Long> nearlyHourReportIds(int minutes);

    void calcMachineDate(Long reportId,int endTimeGap);

    void insertReportApplicationSummaryEntity(Long reportId);

}
