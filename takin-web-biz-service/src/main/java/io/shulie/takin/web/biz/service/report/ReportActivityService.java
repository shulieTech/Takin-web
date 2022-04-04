package io.shulie.takin.web.biz.service.report;

public interface ReportActivityService {

    void startSync(String reportId);

    void syncActivity(String reportId);
}
