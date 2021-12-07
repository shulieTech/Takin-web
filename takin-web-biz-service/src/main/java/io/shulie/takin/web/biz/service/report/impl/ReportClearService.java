package io.shulie.takin.web.biz.service.report.impl;

import javax.annotation.Resource;

import io.shulie.takin.web.data.dao.report.ReportApplicationSummaryDAO;
import io.shulie.takin.web.data.dao.report.ReportBottleneckInterfaceDAO;
import io.shulie.takin.web.data.dao.report.ReportMachineDAO;
import io.shulie.takin.web.data.dao.report.ReportSummaryDAO;
import org.springframework.stereotype.Component;

/**
 * @author qianshui
 * @date 2020/8/12 下午5:20
 */
@Component
public class ReportClearService {

    @Resource
    private ReportApplicationSummaryDAO reportApplicationSummaryDAO;

    @Resource
    private ReportBottleneckInterfaceDAO reportBottleneckInterfaceDAO;

    @Resource
    private ReportMachineDAO reportMachineDAO;

    @Resource
    private ReportSummaryDAO reportSummaryDAO;

    public void clearReportData(Long reportId) {
        if (reportId == null) {
            return;
        }
        reportBottleneckInterfaceDAO.deleteByReportId(reportId);
        reportApplicationSummaryDAO.deleteByReportId(reportId);
        reportSummaryDAO.deleteByReportId(reportId);
        reportMachineDAO.deleteByReportId(reportId);
    }
}

