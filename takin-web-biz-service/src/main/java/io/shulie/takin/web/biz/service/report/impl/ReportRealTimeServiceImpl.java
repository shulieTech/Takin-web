package io.shulie.takin.web.biz.service.report.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.pamirs.pradar.log.parser.trace.RpcEntry;
import com.pamirs.pradar.log.parser.trace.RpcStack;
import com.pamirs.pradar.log.parser.utils.ResultCodeUtils;
import com.pamirs.takin.entity.dao.linkmanage.TBusinessLinkManageTableMapper;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDetailDTO;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.RpcType;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageIdVO;
import io.shulie.takin.cloud.sdk.impl.scene.manage.CloudSceneApiImpl;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp.SceneBusinessActivityRefResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.response.report.ReportLinkDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.biz.utils.business.script.ScriptDebugUtil;
import io.shulie.takin.web.common.domain.PradarWebRequest;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/8/17 下午8:22
 */
@Service
@Slf4j
public class ReportRealTimeServiceImpl implements ReportRealTimeService {

    @Resource
    CloudSceneApiImpl cloudSceneApi;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TraceClient traceClient;

    @Autowired
    private SceneTaskService sceneTaskService;

    @Autowired
    private BusinessLinkManageDAO businessLinkManageDAO;

    @Override
    public PageInfo<ReportTraceDTO> getReportLinkList(Long reportId, Long sceneId, Long startTime,
        Integer type, int current,
        int pageSize) {
        if (startTime == null) {
            return new PageInfo<>(Lists.newArrayList());
        }
        if (reportId == null) {
            reportId = sceneTaskService.getReportIdFromCache(sceneId);
            if (reportId == null) {
                log.warn("get report id by sceneId is empty,sceneId：{}", sceneId);
            }
        }
        // 取延迟1分钟时间 前5分钟数据 因为 agent上报数据需要1分钟计算出来
        return getReportTraceDtoList(reportId, sceneId, System.currentTimeMillis() - 6 * 60 * 1000L,
            System.currentTimeMillis() - 1 * 60 * 1000L, type, current, pageSize);
    }

    @Override
    public PageInfo<ReportTraceDTO> getReportLinkListByReportId(Long reportId, Integer type, int current,
        int pageSize) {
        ReportDetailOutput response = reportService.getReportByReportId(reportId);
        ReportDetailDTO reportDetail = BeanUtil.copyProperties(response, ReportDetailDTO.class);
        if (reportDetail == null || reportDetail.getStartTime() == null) {
            return new PageInfo<>(Lists.newArrayList());
        }
        long startTime = DateUtil.parseSecondFormatter(reportDetail.getStartTime()).getTime() - 5 * 60 * 1000L;
        // 如果reportDetail.getEndTime()为空，取值5min,考虑到取当前时间的话，后续可能会查太多数据
        long endTime = reportDetail.getEndTime() != null ? (reportDetail.getEndTime().getTime() + 5 * 60 * 1000L) : (startTime + 10 * 60 * 1000L);
        return getReportTraceDtoList(reportId, reportDetail.getSceneId(), startTime, endTime, type, current, pageSize);
    }

    @Override
    public ReportLinkDetailResponse getLinkDetail(String traceId, Integer amdbReportTraceId) {
        // 请求amdb, 获得调用链
        PradarWebRequest pradarRequest = new PradarWebRequest();
        pradarRequest.setHttpMethod(HttpMethod.GET);
        pradarRequest.setTraceId(traceId);
        RpcStack rpcStack = traceClient.getTraceDetailById(traceId);

        // 构造响应出参
        ReportLinkDetailResponse response = new ReportLinkDetailResponse();
        if (rpcStack == null || CollectionUtils.isEmpty(rpcStack.getRpcEntries())) {
            log.error("amdb返回的流量明细为空！响应体RpcStack：{}", JSON.toJSONString(rpcStack));
            //throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_LINK_DETAIL_THIRD_PARTY_ERROR, "amdb返回的流量明细为空！");
            response.setTraces(Lists.newArrayList());
            return response;
        }

        // 是否是压测流量判断
        RpcEntry rpcEntry = rpcStack.getRpcEntries().get(0);
        if (rpcEntry != null) {
            response.setClusterTest(rpcEntry.isClusterTest());
        }

        response.setStartTime(rpcStack.getStartTime());
        response.setEntryHostIp(rpcStack.getRootIp());

        List<ReportTraceDetailDTO> vos = Lists.newArrayList();
        BiMap<Integer, Integer> node = HashBiMap.create();
        AtomicInteger integer = new AtomicInteger(0);
        List<ReportTraceDetailDTO> dto = this.coverEntryList(0L, Lists.newArrayList(),
            rpcStack.getRpcEntries(), vos, node, -1, integer);

        List<ReportTraceDetailDTO> result = Lists.newArrayList();
        this.coverResult(dto, amdbReportTraceId, result);

        response.setTraces(result);
        response.setTotalCost(rpcStack.getTotalCost());
        return response;
    }

    /**
     * dto 列表扁平化
     *
     * @param dtoList trace 列表
     * @param id      reportTraceDetai id
     * @param result  处理后的 trace 列表
     */
    private void coverResult(List<ReportTraceDetailDTO> dtoList, Integer id, List<ReportTraceDetailDTO> result) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }

        if (id == 0) {
            if (dtoList.get(0).getNextNodes() != null) {
                dtoList.get(0).getNextNodes().forEach(dto ->
                    dto.setNextNodes(dto.getNextNodes() != null
                        && dto.getNextNodes().size() > 0 ? Lists.newArrayList() : null));
            }
            result.addAll(dtoList);
            return;
        }

        for (ReportTraceDetailDTO dto : dtoList) {
            if (CollectionUtils.isEmpty(dto.getNextNodes())) {
                continue;
            }

            if (dto.getId().equals(id)) {
                if (dto.getNextNodes() != null) {
                    result.addAll(dto.getNextNodes());
                }
            }

            this.coverResult(dto.getNextNodes(), id, result);
        }
    }

    private List<ReportTraceDetailDTO> coverEntryList(long startTime, List<String> convertedEntriesList,
        List<RpcEntry> rpcEntries, List<ReportTraceDetailDTO> vos,
        BiMap<Integer, Integer> node, Integer id, AtomicInteger integer) {
        if (CollectionUtils.isEmpty(rpcEntries)) {
            return null;
        }
        return rpcEntries.stream().map(rpcEntry -> {
            String convertedEntriesKey = rpcEntry.getAppName() + "|" + rpcEntry.getServiceName() + "|" +
                rpcEntry.getMethodName() + "|" + rpcEntry.getRpcType() + "|" + rpcEntry.getMiddlewareName() +
                "|" + rpcEntry.getRpcId();
            if (convertedEntriesList.contains(convertedEntriesKey)) {
                return null;
            }
            convertedEntriesList.add(convertedEntriesKey);
            ReportTraceDetailDTO reportTraceDetailDTO = new ReportTraceDetailDTO();
            reportTraceDetailDTO.setId(integer.getAndIncrement());
            node.forcePut(id, reportTraceDetailDTO.getId());
            // 原始层级
            reportTraceDetailDTO.setCostTime(rpcEntry.getCost());
            reportTraceDetailDTO.setApplicationName(rpcEntry.getAppName());
            reportTraceDetailDTO.setInterfaceName(rpcEntry.getServiceName());
            long offset = 0L;
            for (RpcEntry entry : rpcEntries) {
                String tempKey = entry.getAppName() + "|" + entry.getServiceName() + "|" +
                    entry.getMethodName() + "|" + entry.getRpcType() + "|" + entry.getMiddlewareName() +
                    "|" + entry.getRpcId();
                if (tempKey.equals(convertedEntriesKey)) {
                    break;
                }
                offset += entry.getCost();
            }
            if ("0".equals(rpcEntry.getRpcId()) || rpcEntry.isAsync()) {
                reportTraceDetailDTO.setOffsetStartTime(0L);
            } else {
                reportTraceDetailDTO.setOffsetStartTime(startTime + offset);
            }
            reportTraceDetailDTO.setParams(buildParams(rpcEntry));
            reportTraceDetailDTO.setSucceeded(ResultCodeUtils.isOk(rpcEntry.getResultCode()));
            reportTraceDetailDTO.setRpcId(rpcEntry.getRpcId());
            reportTraceDetailDTO.setAgentId(rpcEntry.getClientAgentId());
            reportTraceDetailDTO.setEntryHostIp(rpcEntry.getClientIp());
            reportTraceDetailDTO.setClusterTest(rpcEntry.isClusterTest());
            // 客户端IP和服务端IP如果两个都有值那就取服务端IP，如果只有一个有值那就取有值的那个
            if (StringUtils.isNotBlank(rpcEntry.getClientIp()) && StringUtils.isNotBlank(rpcEntry.getServerIp())) {
                reportTraceDetailDTO.setNodeIp(rpcEntry.getServerIp());
            } else if (StringUtils.isNotBlank(rpcEntry.getClientIp())) {
                reportTraceDetailDTO.setNodeIp(rpcEntry.getClientIp());
            } else if (StringUtils.isNotBlank(rpcEntry.getServerIp())) {
                reportTraceDetailDTO.setNodeIp(rpcEntry.getServerIp());
            }
            reportTraceDetailDTO.setResponse(rpcEntry.getResponse());
            reportTraceDetailDTO.setNodeSuccess(true);
            if (!reportTraceDetailDTO.getSucceeded()) {
                // 向上递归
                setParentNode(vos, node, reportTraceDetailDTO.getId());
            }
            vos.add(reportTraceDetailDTO);
            reportTraceDetailDTO.setNextNodes(
                coverEntryList(reportTraceDetailDTO.getOffsetStartTime(), convertedEntriesList,
                    rpcEntry.getRpcEntries(), vos, node,
                    reportTraceDetailDTO.getId(), integer));
            //rpcEntry = null;
            return reportTraceDetailDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void setParentNode(List<ReportTraceDetailDTO> vos, BiMap<Integer, Integer> node, Integer id) {
        // 层级获取
        List<Integer> nodes = Lists.newArrayList();
        while (node.containsValue(id)) {
            id = node.inverse().get(id);
            nodes.add(id);
        }
        if (vos != null && vos.size() > 0) {
            vos.forEach(vo -> {
                if (nodes.contains(vo.getId())) {
                    vo.setNodeSuccess(false);
                }
            });
        }
    }

    private String buildParams(RpcEntry item) {
        if (item.getRpcType() == RpcType.DB.getValue()) {
            if (StringUtils.isNotBlank(item.getServerCallbackMsg())) {
                return item.getServerCallbackMsg() + "\n" + item.getRequest();
            }
            if (StringUtils.isNotBlank(item.getClientCallbackMsg())) {
                return item.getClientCallbackMsg() + "\n" + item.getRequest();
            }
        }
        return item.getRequest();
    }

    private PageInfo<ReportTraceDTO> getReportTraceDtoList(Long reportId, Long sceneId, Long startTime, Long endTime, Integer type, int current, int pageSize) {
        // 查询场景下的业务活动信息
        SceneManageWrapperResp response = cloudSceneApi.getSceneDetail(new SceneManageIdReq() {{
            setId(sceneId);
        }});
        List<SceneBusinessActivityRefResp> businessActivityConfig = response.getBusinessActivityConfig();
        List<Long> businessActivityIdList = businessActivityConfig.stream().
            map(SceneBusinessActivityRefResp::getBusinessActivityId).collect(Collectors.toList());

        // entryList 获得
        List<EntranceRuleDTO> entranceList = this.getEntryListByBusinessActivityIds(businessActivityIdList);

        TraceInfoQueryDTO traceInfoQueryDTO = new TraceInfoQueryDTO();

        traceInfoQueryDTO.setStartTime(startTime);
        traceInfoQueryDTO.setEndTime(endTime);
        traceInfoQueryDTO.setReportId(reportId);

        if (type != null) {
            traceInfoQueryDTO.setType(String.valueOf(type));
        }

        //traceInfoQueryDTO.setEntranceList(entranceList);
        traceInfoQueryDTO.setEntranceRuleDTOS(entranceList);
        traceInfoQueryDTO.setPageNum(current);
        traceInfoQueryDTO.setPageSize(pageSize);
        PagingList<EntryTraceInfoDTO> entryTraceInfoDtoPagingList = traceClient.listEntryTraceInfo(traceInfoQueryDTO);
        if (entryTraceInfoDtoPagingList.isEmpty()) {
            return new PageInfo<>(Collections.emptyList());
        }

        List<ReportTraceDTO> collect = entryTraceInfoDtoPagingList.getList().stream().map(traceInfo -> {
            ReportTraceDTO traceDTO = new ReportTraceDTO();
            traceDTO.setInterfaceName(traceInfo.getEntry());
            traceDTO.setApplicationName(buildAppName(traceInfo));
            traceDTO.setSucceeded(ResultCodeUtils.isOk(traceInfo.getStatus()));
            traceDTO.setTotalRt(traceInfo.getProcessTime());
            traceDTO.setStartTime(new Date(traceInfo.getStartTime()));
            traceDTO.setTraceId(traceInfo.getTraceId());

            // resultCode 判断, 赋值
            ScriptDebugRequestListResponse requestListStatusResponse = ScriptDebugUtil.getRequestListStatusResponse(
                traceInfo.getResultCode(), traceInfo.getAssertResult());

            traceDTO.setResponseStatus(requestListStatusResponse.getResponseStatus());
            traceDTO.setResponseStatusDesc(requestListStatusResponse.getResponseStatusDesc());
            traceDTO.setAssertDetailList(requestListStatusResponse.getAssertDetailList());

            traceDTO.setRequestBody(traceInfo.getRequest());
            traceDTO.setResponseBody(traceInfo.getResponse());

            return traceDTO;
        }).collect(Collectors.toList());
        PageInfo<ReportTraceDTO> reportTraceDTOPageInfo = new PageInfo<>(collect);
        reportTraceDTOPageInfo.setTotal(entryTraceInfoDtoPagingList.getTotal());
        return reportTraceDTOPageInfo;
    }

    /**
     * 业务活动ids, 获得 entryList
     *
     * @param businessActivityIds 业务活动ids
     * @return entryList
     */
    @Override
    public List<EntranceRuleDTO> getEntryListByBusinessActivityIds(List<Long> businessActivityIds) {
        // 查询入口集合
        //List<BusinessLinkManageTable> businessLinkManageTableList = tBusinessLinkManageTableMapper.selectBussinessLinkByIdList(businessActivityIds);
        List<BusinessLinkResult> results = businessLinkManageDAO.getListByIds(businessActivityIds);
        List<EntranceRuleDTO> entranceList = Lists.newArrayList();
        for (BusinessLinkResult result : results) {
            EntranceRuleDTO dto = new EntranceRuleDTO();
            String entrance = result.getEntrace();
            if (ActivityUtil.isNormalBusiness(result.getType()) && entrance.contains("http")) {
                entrance = entrance.substring(entrance.indexOf("http"));
            }
            dto.setEntrance(entrance);
            //entranceList.add(entrance);
            dto.setBusinessType(result.getType());
            entranceList.add(dto);
        }
        return entranceList;
    }

    private String buildAppName(EntryTraceInfoDTO takin) {
        StringBuilder builder = new StringBuilder();
        builder.append(takin.getAppName());
        String entry = takin.getEntry();
        if (StringUtils.isNotBlank(entry) && entry.contains("http")) {
            entry = entry.replace("http://", "");
            entry = entry.replace("https://", "");
            if (entry.contains("/")) {
                entry = entry.substring(0, entry.indexOf("/"));
            }
            if (entry.contains(":")) {
                entry = entry.substring(0, entry.indexOf(":"));
            }
            builder.append("(").append(entry).append(")");
        }
        return builder.toString();
    }

}
