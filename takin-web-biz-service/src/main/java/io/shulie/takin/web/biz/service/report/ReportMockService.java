package io.shulie.takin.web.biz.service.report;

import io.shulie.takin.web.biz.pojo.request.report.ReportMockRequest;

public interface ReportMockService {

    /**
     * 从AMDB中查询出当时数据，再入库
     * @param request
     * @return
     */
    void saveReportMockData(ReportMockRequest request);
}
