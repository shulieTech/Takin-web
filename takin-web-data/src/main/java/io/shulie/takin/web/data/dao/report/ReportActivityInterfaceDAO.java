package io.shulie.takin.web.data.dao.report;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.report.ReportActivityInterfaceEntity;
import io.shulie.takin.web.data.param.report.ReportActivityInterfaceQueryParam;

public interface ReportActivityInterfaceDAO {

    boolean batchInsert(List<ReportActivityInterfaceEntity> entityList);

    List<ReportActivityInterfaceEntity> selectByReportId(String reportId);

    List<ReportActivityInterfaceEntity> queryByParam(ReportActivityInterfaceQueryParam param);

    void updateInterfaceCostPercent(List<ReportActivityInterfaceEntity> entities);
}
