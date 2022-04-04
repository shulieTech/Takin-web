package io.shulie.takin.web.amdb.api;

import java.util.List;

import io.shulie.takin.web.amdb.bean.query.report.ReportQueryDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportActivityDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportActivityInterfaceDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportInterfaceMetricsDTO;

public interface ReportClient {

    List<ReportActivityDTO> listReportActivity(ReportQueryDTO query);

    List<ReportActivityInterfaceDTO> listReportActivityInterface(ReportQueryDTO query);

    List<ReportInterfaceMetricsDTO> listReportInterfaceMetrics(ReportQueryDTO query);

    void createReportTraceTable(Long reportId);

    void startAnalyze(Long reportId);
}
