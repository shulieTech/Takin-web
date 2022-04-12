package io.shulie.takin.web.data.dao.report;

public interface ReportTaskDAO {

    void startAnalyze(Long reportId);

    void startSync(String reportId);

    void syncSuccess(String reportId);
}
