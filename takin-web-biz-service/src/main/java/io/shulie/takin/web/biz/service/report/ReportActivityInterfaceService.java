package io.shulie.takin.web.biz.service.report;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceInterfaceDTO;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityInterfaceEntity;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceInterfaceRequest;
import org.apache.commons.lang3.tuple.Pair;

public interface ReportActivityInterfaceService {

    void syncActivityInterface(String reportId);

    Pair<List<ReportPerformanceInterfaceDTO>, Long> queryInterfaceByRequest(ReportPerformanceInterfaceRequest request);

    List<ReportPerformanceInterfaceDTO> queryTop5ByRequest(ReportPerformanceInterfaceRequest request);

    List<ReportActivityInterfaceEntity> selectByReportId(String reportId);
}
