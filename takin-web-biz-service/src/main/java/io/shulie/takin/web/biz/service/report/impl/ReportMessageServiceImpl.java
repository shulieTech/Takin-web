package io.shulie.takin.web.biz.service.report.impl;

import com.pamirs.takin.entity.domain.dto.report.ReportMessageDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageStatusCodeDTO;
import io.shulie.takin.adapter.api.model.request.report.ReportCostTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageCodeReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageDetailReq;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.service.report.ReportMessageService;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportMessageServiceImpl implements ReportMessageService {

    @Autowired
    private AmdbClientProperties properties;

    private static final String AMDB_ENGINE_PRESSURE_QUERY_CODE_LIST_PATH = "/amdb/db/api/tracePressure/queryStatusCode";

    private static final String AMDB_ENGINE_PRESSURE_QUERY_ONETRACE_PATH = "/amdb/db/api/tracePressure/queryOneTrace";

    private static final String AMDB_ENGINE_PRESSURE_QUERY_COSTCOUNT_PATH = "/amdb/db/api/tracePressure/queryCostCount";

    @Override
    public List<ReportMessageStatusCodeDTO> getStatusCodeList(ReportMessageCodeReq req) {
        HttpMethod httpMethod = HttpMethod.GET;
        req.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        req.setEnvCode(WebPluginUtils.traceEnvCode());
        AmdbResult<List<ReportMessageStatusCodeDTO>> response = AmdbHelper.builder().httpMethod(httpMethod)
                .url(properties.getUrl().getAmdb() + AMDB_ENGINE_PRESSURE_QUERY_CODE_LIST_PATH)
                .param(req)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询enginePressure数据")
                .list(ReportMessageStatusCodeDTO.class);
        return response.getData();
    }

    @Override
    public ReportMessageDetailDTO getOneTraceDetail(ReportMessageDetailReq req) {
        HttpMethod httpMethod = HttpMethod.GET;
        req.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        req.setEnvCode(WebPluginUtils.traceEnvCode());
        AmdbResult<ReportMessageDetailDTO> response = AmdbHelper.builder().httpMethod(httpMethod)
                .url(properties.getUrl().getAmdb() + AMDB_ENGINE_PRESSURE_QUERY_ONETRACE_PATH)
                .param(req)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询enginePressure数据")
                .one(ReportMessageDetailDTO.class);
        return response.getData();
    }

    @Override
    public Integer getRequestCountByCost(ReportCostTrendQueryReq req) {
        HttpMethod httpMethod = HttpMethod.GET;
        req.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        req.setEnvCode(WebPluginUtils.traceEnvCode());
        AmdbResult<Integer> response = AmdbHelper.builder().httpMethod(httpMethod)
                .url(properties.getUrl().getAmdb() + AMDB_ENGINE_PRESSURE_QUERY_COSTCOUNT_PATH)
                .param(req)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询enginePressure数据")
                .one(Integer.class);
        return response.getData();
    }
}
