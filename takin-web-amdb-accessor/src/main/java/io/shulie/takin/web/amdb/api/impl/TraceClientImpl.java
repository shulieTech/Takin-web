package io.shulie.takin.web.amdb.api.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.pamirs.pradar.log.parser.ProtocolParserFactory;
import com.pamirs.pradar.log.parser.trace.RpcBased;
import com.pamirs.pradar.log.parser.trace.RpcStack;
import com.pamirs.takin.common.util.DateUtils;
import io.shulie.amdb.common.request.trace.EntryTraceQueryParam;
import io.shulie.surge.data.deploy.pradar.link.model.TTrackClickhouseModel;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.query.script.QueryLinkDetailDTO;
import io.shulie.takin.web.amdb.bean.query.trace.*;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
import io.shulie.takin.web.amdb.bean.result.trace.TraceMetrics;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
@Component
@Slf4j
public class TraceClientImpl implements TraceClient {

    /**
     * 获得调试对应的请求流量明细
     * 路由
     */
    private static final String ENTRY_TRACE_BY_TASK_ID_PATH_V2 = "/amdb/trace/getDebugTraceList";

    private static final String QUERY_TRACE_PATH = "/amdb/trace/getTraceDetail?traceId=@TraceId@";

    private static final String ENTRY_TRACE_PATH = "/amdb/trace/getEntryTraceList";

    /**
     * trace日志
     */
    private static final String ENTRY_TRACE_LOG_PATH = "/amdb/trace/getAllTraceList";

    private static final String DATA_CALIBRATION_PATH = "/amdb/trace/compensate";

    private static final String TRACE_METRIC_GET_SQL_STATEMENTS = "/amdb/db/api/traceMetric/getSqlStatements";

    private static final String GET_EDGE_IDS_BY_APP_NAMES = "/amdb/link/selectEdgeIdsByAppNames";

    @Autowired
    private AmdbClientProperties properties;

    @Override
    public PagingList<EntryTraceInfoDTO> listEntryTraceByTaskIdV2(QueryLinkDetailDTO dto) {
        Assert.notNull(dto, "参数必须传递!");

        // 结果类型
        if (dto.getResultTypeInt() != null) {
            dto.setResultType(dto.getResultTypeInt());
        }
        // 默认
        dto.setFieldNames("appName,serviceName,methodName,remoteIp,port,resultCode,cost,startTime,traceId");
        dto.setEntranceList(this.getEntryListString(dto.getEntranceRuleDTOS()));
        dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        dto.setEnvCode(WebPluginUtils.traceEnvCode());
        String url = properties.getUrl().getAmdb() + ENTRY_TRACE_BY_TASK_ID_PATH_V2;
        try {
            AmdbResult<List<EntryTraceInfoDTO>> result = AmdbHelper.builder().url(url)
                    .param(dto)
                    .eventName("通过taskId查询链路列表")
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .list(EntryTraceInfoDTO.class);

            return PagingList.of(result.getData(), result.getTotal());

        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public PagingList<EntryTraceInfoDTO> listEntryTraceInfo(TraceInfoQueryDTO query) {
        String url = properties.getUrl().getAmdb() + ENTRY_TRACE_PATH;
        try {
            QueryLinkDetailDTO dto = new QueryLinkDetailDTO();
            BeanUtils.copyProperties(query, dto);
            if (query.getReportId() != null) {
                dto.setTaskId(query.getReportId().toString());
            }
            dto.setEntranceList(this.getEntryListString(query.getEntranceRuleDTOS()));
            dto.setCurrentPage(query.getPageNum());
            dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            dto.setEnvCode(WebPluginUtils.traceEnvCode());
            dto.setFieldNames("appName,serviceName,methodName,remoteIp,port,resultCode,cost,startTime,traceId");
            //固定查询影子链路明细数据
            dto.setQueryType(query.getQueryType());
            //压测流量明细查询，去掉固定查压测流量
            //dto.setClusterTest(1);
            String traceId = query.getTraceId();
            if (StringUtils.isNotBlank(traceId)) {
                dto.setTraceIdList(Arrays.asList(traceId.split(",")));
            }
            dto.setEntranceRuleDTOS(null);
            AmdbResult<List<EntryTraceInfoDTO>> response = AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST)
                    .param(dto)
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .eventName("查询链路列表")
                    .list(EntryTraceInfoDTO.class);
            List<EntryTraceInfoDTO> list = response.getData();
            if (CollUtil.isNotEmpty(list)) {
                list.forEach(entry -> {
                    entry.setEntry(entry.getServiceName());
                    entry.setMethod(entry.getMethodName());
                    entry.setProcessTime(entry.getCost());
                    entry.setId("0");
                    entry.setEndTime(entry.getEndTime());
                    entry.setStatus(entry.getResultCode());
                });
                return PagingList.of(list, response.getTotal());
            }
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
        return PagingList.empty();
    }

    @Override
    public EntryTraceInfoDTO getEntryTraceInfo(TraceInfoQueryDTO query) {
        String url = properties.getUrl().getAmdb() + ENTRY_TRACE_PATH;
        try {
            QueryLinkDetailDTO dto = new QueryLinkDetailDTO();
            BeanUtils.copyProperties(query, dto);
            if (query.getReportId() != null) {
                dto.setTaskId(query.getReportId().toString());
            }
            dto.setEntranceList(this.getEntryListString(query.getEntranceRuleDTOS()));
            dto.setCurrentPage(query.getPageNum());
            dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            dto.setEnvCode(WebPluginUtils.traceEnvCode());
            dto.setFieldNames("appName,serviceName,methodName,remoteIp,port,resultCode,cost,startTime,traceId");
            //固定查询影子链路明细数据
            dto.setQueryType(query.getQueryType());
            //压测流量明细查询，去掉固定查压测流量
            //dto.setClusterTest(1);
            dto.setEntranceRuleDTOS(null);
            String traceId = query.getTraceId();
            if (StringUtils.isNotBlank(traceId)) {
                dto.setTraceIdList(Arrays.asList(traceId.split(",")));
            }
            dto.setPageSize(1);
            AmdbResult<List<EntryTraceInfoDTO>> response = AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST)
                    .param(dto)
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .eventName("查询链路列表")
                    .list(EntryTraceInfoDTO.class);
            List<EntryTraceInfoDTO> list = response.getData();
            if (CollUtil.isNotEmpty(list)) {
                list.forEach(entry -> {
                    entry.setEntry(entry.getServiceName());
                    entry.setMethod(entry.getMethodName());
                    entry.setProcessTime(entry.getCost());
                    entry.setId("0");
                    entry.setEndTime(entry.getEndTime());
                    entry.setStatus(entry.getResultCode());
                });
                return list.get(0);
            }
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public RpcStack getTraceDetailById(String traceId, String... times) {
        try {
            String url = properties.getUrl().getAmdb() + QUERY_TRACE_PATH.replace("@TraceId@", traceId);
            url = url + "&tenantAppKey=" + WebPluginUtils.traceTenantAppKey() + "&envCode=" + WebPluginUtils.traceEnvCode();
            if (times.length == 2) {
                url += "&startTime=" + times[0] + "&endTime=" + times[1];
            }
            AmdbResult<List<RpcBased>> amdbResponse = AmdbHelper.builder().url(url)
                    .eventName("查询Trace调用栈明细")
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .list(RpcBased.class);
            if (CollectionUtils.isEmpty(amdbResponse.getData())) {
                return null;
            }
            return ProtocolParserFactory.getFactory().parseRpcStackByRpcBase(traceId, amdbResponse.getData());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<RpcBased> getTraceBaseById(String traceId) {
        try {
            String url = properties.getUrl().getAmdb() + QUERY_TRACE_PATH.replace("@TraceId@", traceId);
            url = url + "&tenantAppKey=" + WebPluginUtils.traceTenantAppKey() + "&envCode=" + WebPluginUtils.traceEnvCode();
            AmdbResult<List<RpcBased>> amdbResponse = AmdbHelper.builder().url(url)
                    .eventName("查询Trace调用栈明细")
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .list(RpcBased.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    /**
     * 企业版本使用
     * @param query
     * @return
     */
    @Override
    public PagingList<TTrackClickhouseModel> listTraceLog(TraceLogQueryDTO query) {
        String url = properties.getUrl().getAmdb() + ENTRY_TRACE_LOG_PATH;
        EntryTraceQueryParam param = new EntryTraceQueryParam();
        param.setAppNames(query.getAppNames());
        if (StringUtils.isNotBlank(query.getAppName())) {
            param.setAppName(query.getAppName());
        }

        if (StringUtils.isNotBlank(query.getServiceName())) {
            param.setServiceName(query.getServiceName());
        }
        if (StringUtils.isNotBlank(query.getTraceId())) {
            param.setTraceIdList(Sets.newHashSet(query.getTraceId()));
        }
        if (StringUtils.isBlank(query.getStartTime()) && StringUtils.isBlank(query.getEndTime())) {
            // 查一天的
            return PagingList.empty();
        }
        if (StringUtils.isNotBlank(query.getStartTime())) {
            param.setStartTime(DateUtils.transferTime(query.getStartTime()).getTime());
        }
        if (StringUtils.isNotBlank(query.getEndTime())) {
            param.setEndTime(DateUtils.transferTime(query.getEndTime()).getTime());
        }

        param.setCurrentPage(query.getCurrentPage() + 1);
        param.setPageSize(query.getPageSize());
        param.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        param.setEnvCode(WebPluginUtils.traceEnvCode());
        try {
            AmdbResult<List<TTrackClickhouseModel>> response = AmdbHelper.builder().url(url)
                    .httpMethod(HttpMethod.POST)
                    .param(param)
                    .exception(TakinWebExceptionEnum.APPLICATION_TRACE_LOG_AGENT_ERROR)
                    .eventName("查询trace日志列表")
                    .list(TTrackClickhouseModel.class);
            return PagingList.of(response.getData(), response.getTotal());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    /**
     * entryList 转换一下
     *
     * @param entranceList 入口列表
     * @return 转换后的 入口列表 字符串形式
     */
    private String getEntryListString(List<EntranceRuleDTO> entranceList) {
        if (CollectionUtil.isEmpty(entranceList)) {
            return "";
        }

        return entranceList.stream().map(entrance -> {
            if (ActivityUtil.isNormalBusiness(entrance.getBusinessType())) {
                EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(entrance.getEntrance());
                return String.format("%s#%s#%s#%s",entrance.getAppName(),
                    entranceJoinEntity.getServiceName(),
                    entranceJoinEntity.getMethodName(), entranceJoinEntity.getRpcType());
            } else {
                EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertVirtualEntrance(entrance.getEntrance());
                return String.format("%s#%s#%s#%s", "", entranceJoinEntity.getVirtualEntrance(),
                    StringUtils.defaultIfBlank(entranceJoinEntity.getMethodName(), ""), entranceJoinEntity.getRpcType());
            }

        }).collect(Collectors.joining(AppConstants.COMMA));
    }

    @Override
    public String dataCalibration(DataCalibrationDTO dataCalibration) {
        String url = properties.getUrl().getAmdb() + DATA_CALIBRATION_PATH;
        return AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST).param(dataCalibration)
            .exception(TakinWebExceptionEnum.SCENE_REPORT_DATA_CALIBRATION)
            .eventName("压测报告数据校准").one(String.class).getData();
    }

    @Override
    public List<TraceMetrics> getSqlStatements(TraceMetricsRequest traceMetricsRequest) {
        String url = properties.getUrl().getAmdb() + TRACE_METRIC_GET_SQL_STATEMENTS;
        Response response = AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST)
                .param(traceMetricsRequest)
                .exception(TakinWebExceptionEnum.SCENE_REPORT_DATA_CALIBRATION)
                .eventName("应用趋势图查询").one(Response.class).getData();
        if (!response.isSuccess()) {
            return Collections.emptyList();
        }
        return (List<TraceMetrics>) response.getData();
    }

    @Override
    public List<String> getEdgeIdsByAppNames(List<String> appNames) {
        String url = properties.getUrl().getAmdb() + GET_EDGE_IDS_BY_APP_NAMES;
        try {
            Response response = AmdbHelper.builder().url(url).httpMethod(HttpMethod.POST)
                    .param(appNames)
                    .exception(TakinWebExceptionEnum.SCENE_REPORT_DATA_CALIBRATION)
                    .eventName("应用趋势图查询").one(Response.class).getData();
            return response.getData() == null ? Collections.emptyList() : JSON.parseArray(JSON.toJSONString(response.getData()), String.class);
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_DATA_CALIBRATION, e.getMessage());
        }
    }
}
