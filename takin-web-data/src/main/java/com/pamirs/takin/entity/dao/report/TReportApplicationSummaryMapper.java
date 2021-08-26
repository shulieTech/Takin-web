package com.pamirs.takin.entity.dao.report;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.report.ReportApplicationSummary;

public interface TReportApplicationSummaryMapper {

    int insert(ReportApplicationSummary record);

    int insertOrUpdate(ReportApplicationSummary record);

    int insertBatch(List<ReportApplicationSummary> records);

    List<ReportApplicationSummary> selectByParam(ReportApplicationSummary example);

    ReportApplicationSummary selectByPrimaryKey(Long id);

    Map<String, Object> selectCountByReportId(Long reportId);

    void deleteByReportId(Long reportId);
}
