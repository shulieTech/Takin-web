package io.shulie.takin.web.data.dao.report;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.report.ReportInterfaceMetricsEntity;
import io.shulie.takin.web.data.param.report.ReportInterfaceMetricsQueryParam;

public interface ReportInterfaceMetricsDAO {

    boolean batchInsert(List<ReportInterfaceMetricsEntity> entityList);

    List<ReportInterfaceMetricsEntity> queryByParam(ReportInterfaceMetricsQueryParam param);
}
