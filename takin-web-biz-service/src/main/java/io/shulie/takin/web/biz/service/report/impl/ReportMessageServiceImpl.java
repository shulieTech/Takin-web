package io.shulie.takin.web.biz.service.report.impl;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.common.enums.ResponseResultEnum;
import com.pamirs.takin.common.enums.ResultCodeEnum;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageQueryDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageStatusCodeDTO;
import io.shulie.takin.adapter.api.model.request.report.ReportCostTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageCodeReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageDetailReq;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.service.report.ReportMessageService;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ReportMessageServiceImpl implements ReportMessageService {

    @Autowired
    private AmdbClientProperties properties;
    @Resource
    private RedisClientUtil redisClientUtil;

    private static final String AMDB_ENGINE_PRESSURE_QUERY_CODE_LIST_PATH = "/amdb/db/api/tracePressure/queryStatusCode";

    private static final String AMDB_ENGINE_PRESSURE_QUERY_ONETRACE_PATH = "/amdb/db/api/tracePressure/queryOneTrace";

    private static final String AMDB_ENGINE_PRESSURE_QUERY_COSTCOUNT_PATH = "/amdb/db/api/tracePressure/queryCostCount";

    private static final String reportMessageCodeData = "report:vlt:messageCodeData:%s:%s";

    private static final String reportMessageDetailData = "report:vlt:messageDetailData:%s:%s:%s";

    @Autowired
    private InfluxWriter influxWriter;

    @Override
    public List<ReportMessageStatusCodeDTO> getStatusCodeList(ReportMessageCodeReq req) {
        String redisKey = String.format(reportMessageCodeData, req.getJobId(), req.getServiceName());
        if (redisClientUtil.hasKey(redisKey)) {
            String redisValue = redisClientUtil.getString(redisKey);
            return JSON.parseArray(redisValue, ReportMessageStatusCodeDTO.class);
        }
        HttpMethod httpMethod = HttpMethod.GET;
        req.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        req.setEnvCode(WebPluginUtils.traceEnvCode());
        AmdbResult<List<ReportMessageStatusCodeDTO>> response = AmdbHelper.builder().httpMethod(httpMethod)
                .url(properties.getUrl().getAmdb() + AMDB_ENGINE_PRESSURE_QUERY_CODE_LIST_PATH)
                .param(req)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询enginePressure数据")
                .list(ReportMessageStatusCodeDTO.class);
        List<ReportMessageStatusCodeDTO> codeList = response.getData();
        //ResultCode转化为ResponseResult
        List<ReportMessageStatusCodeDTO> newCodeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(codeList)) {
            Set<String> codeSet = new HashSet<>();
            for (ReportMessageStatusCodeDTO dto : codeList) {
                ResultCodeEnum codeEnum = ResultCodeEnum.getResultCodeEnumByCode(dto.getStatusCode());
                if (codeEnum == null) {
                    continue;
                }
                if (codeSet.contains(codeEnum.getResult().getCode())) {
                    continue;
                }
                codeSet.add(codeEnum.getResult().getCode());
                ReportMessageStatusCodeDTO newDto = new ReportMessageStatusCodeDTO();
                newDto.setStatusCode(codeEnum.getResult().getCode());
                newDto.setStatusName(codeEnum.getResult().getDesc());
                newCodeList.add(newDto);
            }
        }
        if (CollectionUtils.isEmpty(newCodeList)) {
            ReportMessageStatusCodeDTO defaultDTO = new ReportMessageStatusCodeDTO();
            defaultDTO.setStatusCode(ResponseResultEnum.RESP_SUCCESS.getCode());
            defaultDTO.setStatusName(ResponseResultEnum.RESP_SUCCESS.getDesc());
            newCodeList.add(defaultDTO);
        }
        return newCodeList;
    }

    @Override
    public ReportMessageDetailDTO getOneTraceDetail(ReportMessageDetailReq req) {
        String redisKey = String.format(reportMessageDetailData, req.getJobId(), req.getServiceName(), req.getStatusCode());
        if (redisClientUtil.hasKey(redisKey)) {
            String redisValue = redisClientUtil.getString(redisKey);
            return JSON.parseObject(redisValue, ReportMessageDetailDTO.class);
        }
        List<String> resultCode = new ArrayList<>();
        //ResponseResult转化为ResultCode
        for (ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()) {
            if (resultCodeEnum.getResult().getCode().equals(req.getStatusCode())) {
                resultCode.add(resultCodeEnum.getCode());
            }
        }
        req.setResultCode(StringUtils.join(resultCode, ","));
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
    public Long getRequestCountByCost(ReportCostTrendQueryReq queryParam) {
        String measurement = InfluxUtil.getMeasurement(queryParam.getJobId(), null, null, null);
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(count) as count from ");
        sql.append(measurement);
        sql.append(" where transaction='" + queryParam.getTransaction() + "'");
        sql.append(" and avg_rt>=" + queryParam.getMinCost());
        sql.append(" and avg_rt<" + queryParam.getMaxCost());
        log.info("TracePressureController#getCostCount execute sql={}", sql);
        ReportMessageQueryDTO messageQueryDTO = influxWriter.querySingle(sql.toString(), ReportMessageQueryDTO.class);
        if (messageQueryDTO == null) {
            return 0L;
        }
        return messageQueryDTO.getCount();
    }
}
