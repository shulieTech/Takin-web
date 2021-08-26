package com.pamirs.takin.entity.dao.report;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.report.ReportSummary;

public interface TReportSummaryMapper {
    int insert(ReportSummary record);

    List<ReportSummary> selectByExample(ReportSummary example);

    ReportSummary selectByPrimaryKey(Long id);

    ReportSummary selectOneByReportId(Long reportId);

    void deleteByReportId(Long reportId);
}
