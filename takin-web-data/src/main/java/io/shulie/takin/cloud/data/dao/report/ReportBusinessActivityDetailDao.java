package io.shulie.takin.cloud.data.dao.report;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;

/**
 * @author moriarty
 */
public interface ReportBusinessActivityDetailDao {

    int insert(ReportBusinessActivityDetail activityDetail);

    int update(ReportBusinessActivityDetail activityDetail);

    ReportBusinessActivityDetailEntity selectById(Long id);

    List<ReportBusinessActivityDetailEntity> selectDetailsByReportId(Long reportId);

    ReportBusinessActivityDetailEntity selectDetailByReportIdAndActivityId(Long reportId, Long activityId);
}
