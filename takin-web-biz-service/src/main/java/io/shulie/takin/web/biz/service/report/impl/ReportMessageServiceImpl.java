package io.shulie.takin.web.biz.service.report.impl;

import cn.hutool.http.HttpUtil;
import com.pamirs.takin.entity.domain.dto.TakinSreBizRiskLogDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportCostDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageStatusCodeDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportProblemCheckDTO;
import io.shulie.takin.adapter.api.model.request.report.ReportCostTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageCodeReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageDetailReq;
import io.shulie.takin.adapter.api.model.request.report.ReportProblemListReq;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.service.report.ReportMessageService;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportMessageServiceImpl implements ReportMessageService {

    @Autowired
    private AmdbClientProperties properties;

    @Autowired
    private InfluxWriter influxWriter;

    @Value("${takin.sre.url:http://127.0.0.1/takin-sre}")
    private String takinSreUrl;

    private static final String AMDB_ENGINE_PRESSURE_QUERY_CODE_LIST_PATH = "/amdb/db/api/tracePressure/queryStatusCode";

    private static final String AMDB_ENGINE_PRESSURE_QUERY_ONETRACE_PATH = "/amdb/db/api/tracePressure/queryOneTrace";

    private static final String TAKIN_SRE_BIZRISKLOG_PATH = "/api/noAuth/call/query/riskBizLog";

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
    public List<ReportProblemCheckDTO> getProblemCheckList(ReportProblemListReq req) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startTime", req.getStartTime());
        paramMap.put("endTime", req.getEndTime());
        paramMap.put("service", req.getServiceName());
        paramMap.put("tenantCode", WebPluginUtils.traceTenantCode());
        paramMap.put("envCode", WebPluginUtils.traceEnvCode());
        paramMap.put("page", 0);
        paramMap.put("size", 100);
        try {
            String requestResult = HttpUtil.createGet(takinSreUrl + TAKIN_SRE_BIZRISKLOG_PATH)
                    .form(paramMap)
                    .execute()
                    .body();
            TakinSreBizRiskLogDTO logDTO = JsonHelper.json2Bean(requestResult, TakinSreBizRiskLogDTO.class);
            List<ReportProblemCheckDTO> dtoList = new ArrayList<>();
            if (!logDTO.getSuccess() || CollectionUtils.isEmpty(logDTO.getData())) {
                return dtoList;
            }
            for (int i = 0; i < logDTO.getData().size(); i++) {
                ReportProblemCheckDTO dto = new ReportProblemCheckDTO();
                dto.setSeqNo(i + 1);
                dto.setCheckResult(logDTO.getData().get(i).getCalcResult());
                dto.setNodeId(logDTO.getData().get(i).getInvokeId());
                dto.setNodeName(logDTO.getData().get(i).getRiskSubject());
                dto.setCurrentValue(logDTO.getData().get(i).getCurrentValue() + logDTO.getData().get(i).getCurrentValueUnit());
                dto.setTechRiskName(logDTO.getData().get(i).getStandardName());
                dto.setTraceSampling(logDTO.getData().get(i).getTraceSampling());
                dtoList.add(dto);
            }
            return dtoList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Long getRequestCountByCost(ReportCostTrendQueryReq req) {
        String influxDbSql = "select "
                + "sum(count) as count"
                + " from "
                + InfluxUtil.getMeasurement(req.getJobId(), null, null, null)
                + " where transaction = '" + req.getTransaction()
                + "' and avg_rt >= " + req.getMinCost() + " and avg_rt < " + req.getMaxCost();
        ReportCostDTO costDTO = influxWriter.querySingle(influxDbSql, ReportCostDTO.class);
        return (costDTO != null && costDTO.getCount() != null) ? costDTO.getCount() : 0L;
    }
}
