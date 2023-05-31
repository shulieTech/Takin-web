package io.shulie.takin.web.biz.service.report;

import com.pamirs.takin.entity.domain.dto.report.ReportMessageDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageStatusCodeDTO;
import io.shulie.takin.adapter.api.model.request.report.ReportCostTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageCodeReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageDetailReq;

import java.util.List;

public interface ReportMessageService {

    List<ReportMessageStatusCodeDTO> getStatusCodeList(ReportMessageCodeReq req);

    ReportMessageDetailDTO getOneTraceDetail(ReportMessageDetailReq req);

    Long getRequestCountByCost(ReportCostTrendQueryReq req);
}
