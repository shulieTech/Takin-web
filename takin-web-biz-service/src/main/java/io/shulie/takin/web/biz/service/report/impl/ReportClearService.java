package io.shulie.takin.web.biz.service.report.impl;

import javax.annotation.Resource;

import com.pamirs.takin.entity.dao.report.TReportApplicationSummaryMapper;
import com.pamirs.takin.entity.dao.report.TReportBottleneckInterfaceMapper;
import com.pamirs.takin.entity.dao.report.TReportMachineMapper;
import com.pamirs.takin.entity.dao.report.TReportSummaryMapper;
import org.springframework.stereotype.Component;

/**
 * @author qianshui
 * @date 2020/8/12 下午5:20
 */
@Component
public class ReportClearService {

    @Resource
    private TReportApplicationSummaryMapper tReportApplicationSummaryMapper;

    @Resource
    private TReportBottleneckInterfaceMapper tReportBottleneckInterfaceMapper;

    @Resource
    private TReportMachineMapper tReportMachineMapper;

    @Resource
    private TReportSummaryMapper tReportSummaryMapper;

    public void clearReportData(Long reportId) {
        if (reportId == null) {
            return;
        }
        tReportBottleneckInterfaceMapper.deleteByReportId(reportId);
        tReportApplicationSummaryMapper.deleteByReportId(reportId);
        tReportSummaryMapper.deleteByReportId(reportId);
        tReportMachineMapper.deleteByReportId(reportId);
    }
}

