package com.pamirs.takin.entity.dao.report;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.report.ReportBottleneckInterface;
import com.pamirs.takin.entity.domain.vo.report.ReportLocalQueryParam;

public interface TReportBottleneckInterfaceMapper {

    int insertBatch(List<ReportBottleneckInterface> records);

    List<ReportBottleneckInterface> selectByExample(ReportLocalQueryParam queryParam);

    ReportBottleneckInterface selectByPrimaryKey(Long id);

    Long selectCountByReportId(Long reportId);

    void deleteByReportId(Long reportId);
}
