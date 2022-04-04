package io.shulie.takin.web.biz.service.report;

import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceCostTrendDTO;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceCostTrendRequest;

public interface ReportInterfaceMetricsService {

    void syncInterfaceMetrics(String reportId);

    void syncSuccessTask(String reportId);

    ReportPerformanceCostTrendDTO queryCostTrend(ReportPerformanceCostTrendRequest request);
}
