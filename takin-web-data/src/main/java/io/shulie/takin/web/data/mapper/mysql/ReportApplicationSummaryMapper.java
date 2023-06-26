package io.shulie.takin.web.data.mapper.mysql;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ReportApplicationSummaryEntity;

public interface ReportApplicationSummaryMapper extends BaseMapper<ReportApplicationSummaryEntity> {
    int insertOrUpdate(ReportApplicationSummaryEntity entity);

    Map<String, Object> selectCountByReportId(Long reportId);

    int insertOrUpdateTargetTps(ReportApplicationSummaryEntity entity);
}
