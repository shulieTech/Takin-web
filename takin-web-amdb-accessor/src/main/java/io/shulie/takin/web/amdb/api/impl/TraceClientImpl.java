package io.shulie.takin.web.amdb.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.pamirs.pradar.log.parser.ProtocolParserFactory;
import com.pamirs.pradar.log.parser.trace.RpcBased;
import com.pamirs.pradar.log.parser.trace.RpcStack;
import com.pamirs.takin.common.util.DateUtils;
import io.shulie.amdb.common.request.trace.EntryTraceQueryParam;
import io.shulie.surge.data.deploy.pradar.link.model.TTrackClickhouseModel;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.query.script.QueryLinkDetailDTO;
import io.shulie.takin.web.amdb.bean.query.trace.DataCalibrationDTO;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceLogQueryDTO;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
@Component
@Slf4j
public class TraceClientImpl implements TraceClient {

    /**
     * ???????????????????????????????????????
     * ??????
     */
    private static final String ENTRY_TRACE_BY_TASK_ID_PATH_V2 = "/amdb/trace/getDebugTraceList";

    private static final String QUERY_TRACE_PATH = "/amdb/trace/getTraceDetail?traceId=@TraceId@";

    private static final String ENTRY_TRACE_PATH = "/amdb/trace/getEntryTraceList";

    /**
     * trace??????
     */
    private static final String ENTRY_TRACE_LOG_PATH = "/amdb/trace/getAllTraceList";

    private static final String DATA_CALIBRATION_PATH = "/amdb/trace/compensate";

    @Autowired
    private AmdbClientProperties properties;

    @Override
    public PagingList<EntryTraceInfoDTO> listEntryTraceByTaskIdV2(QueryLinkDetailDTO dto) {
        Assert.notNull(dto, "??????????????????!");

        // ????????????
        if (dto.getResultTypeInt() != null) {
            dto.setResultType(dto.getResultTypeInt());
        }
        // ??????
        dto.setFieldNames("appName,serviceName,methodName,remoteIp,port,resultCode,cost,startTime,traceId");
        dto.setEntranceList(this.getEntryListString(dto.getEntranceRuleDTOS()));
        dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        dto.setEnvCode(WebPluginUtils.traceEnvCode());
        String url = properties.getUrl().getAmdb() + ENTRY_TRACE_BY_TASK_ID_PATH_V2;
        try {
            AmdbResult<List<EntryTraceInfoDTO>> result = AmdbHelper.builder().url(url)
                    .param(dto)
                    .eventName("??????taskId??????????????????")
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
            //????????????????????????????????????
            dto.setQueryType(query.getQueryType());
            //??????????????????????????????????????????????????????
            //dto.setClusterTest(1);
            dto.setTraceIdList(query.getTraceId());
            AmdbResult<List<EntryTraceInfoDTO>> response = AmdbHelper.builder().url(url)
                    .param(dto)
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .eventName("??????????????????")
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
            //????????????????????????????????????
            dto.setQueryType(query.getQueryType());
            //??????????????????????????????????????????????????????
            //dto.setClusterTest(1);
            dto.setTraceIdList(query.getTraceId());
            dto.setPageSize(1);
            AmdbResult<List<EntryTraceInfoDTO>> response = AmdbHelper.builder().url(url)
                    .param(dto)
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .eventName("??????????????????")
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
                    .eventName("??????Trace???????????????")
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
                    .eventName("??????Trace???????????????")
                    .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                    .list(RpcBased.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    /**
     * ??????????????????
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
            // ????????????
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
                    .eventName("??????trace????????????")
                    .list(TTrackClickhouseModel.class);
            return PagingList.of(response.getData(), response.getTotal());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    /**
     * entryList ????????????
     *
     * @param entranceList ????????????
     * @return ???????????? ???????????? ???????????????
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
            .eventName("????????????????????????").one(String.class).getData();
    }
}
