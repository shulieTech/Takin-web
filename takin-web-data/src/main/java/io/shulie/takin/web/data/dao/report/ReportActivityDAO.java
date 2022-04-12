package io.shulie.takin.web.data.dao.report;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.report.ReportActivityEntity;

public interface ReportActivityDAO {

    boolean batchInsert(List<ReportActivityEntity> entityList);

    void updateEntranceAvgCost(List<ReportActivityEntity> entities);
}
