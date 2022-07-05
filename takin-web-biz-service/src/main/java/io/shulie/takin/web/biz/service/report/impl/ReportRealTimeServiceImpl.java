package io.shulie.takin.web.biz.service.report.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.entity.domain.dto.report.ReportTraceQueryDTO;
import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.adapter.api.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.web.diff.api.report.ReportApi;
import lombok.extern.slf4j.Slf4j;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.CollectionUtils;

import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.pradar.log.parser.trace.RpcEntry;
import com.pamirs.pradar.log.parser.trace.RpcStack;
import com.pamirs.pradar.log.parser.utils.TraceIdUtil;
import com.pamirs.pradar.log.parser.utils.ResultCodeUtils;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDetailDTO;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.RpcType;

import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.common.enums.trace.TraceNodeAsyncEnum;
import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
import io.shulie.takin.web.common.enums.trace.TraceNodeLogTypeEnum;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.biz.utils.business.script.ScriptDebugUtil;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.web.biz.pojo.response.report.ReportLinkDetailResponse;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneBusinessActivityRefResp;

/**
 * @author qianshui
 * @date 2020/8/17 下午8:22
 */
@Service
@Slf4j
public class ReportRealTimeServiceImpl implements ReportRealTimeService {
    @Resource
    CloudSceneManageApi cloudSceneApi;
    @Resource
    private ReportDao reportDao;
    @Resource
    private TraceClient traceClient;
    @Resource
    private SceneTaskService sceneTaskService;
    @Resource
    private BusinessLinkManageDAO businessLinkManageDAO;
    @Resource
    private ReportApi reportApi;

    @Override
    public PageInfo<ReportTraceDTO> getReportLinkList(ReportTraceQueryDTO queryDTO) {
        if (queryDTO.getStartTime() == null) {
            return new PageInfo<>(Lists.newArrayList());
        }
        Long reportId = queryDTO.getReportId();
        Long sceneId = queryDTO.getSceneId();
        if (reportId == null) {
            reportId = sceneTaskService.getReportIdFromCache(sceneId);
            queryDTO.setReportId(reportId);
            if (reportId == null) {
                log.warn("get report id by sceneId is empty,sceneId：{}", sceneId);
            } else {
                ReportResult report = reportDao.getById(reportId);
                queryDTO.setTaskId(report.getJobId());
            }
        }
        // 取延迟1分钟时间 前5分钟数据 因为 agent上报数据需要1分钟计算出来：改为前端控制
        return getReportTraceDtoList(queryDTO);
    }

    @Override
    public PageInfo<ReportTraceDTO> getReportLinkListByReportId(ReportTraceQueryDTO queryDTO) {
        Long reportId = queryDTO.getReportId();
        ReportResult report = reportDao.getById(reportId);
        if (report == null || report.getStartTime() == null) {
            return new PageInfo<>(Lists.newArrayList());
        }
        queryDTO.setTaskId(report.getJobId());
        Long startTime = queryDTO.getStartTime();
        long reportStartTime = report.getStartTime().getTime() - 5 * 60 * 1000L;
        if (startTime == null || startTime.compareTo(0L) <= 0) {
            queryDTO.setStartTime(reportStartTime);
        }
        // 如果reportDetail.getEndTime()为空，取值5min,考虑到取当前时间的话，后续可能会查太多数据
        Long endTime = queryDTO.getEndTime();
        if (endTime == null || endTime.compareTo(queryDTO.getStartTime()) <= 0) {
            queryDTO.setEndTime(
                report.getEndTime() != null ? (report.getEndTime().getTime() + 5 * 60 * 1000L) : (reportStartTime + 10 * 60 * 1000L));
        }
        queryDTO.setSceneId(report.getSceneId());
        return getReportTraceDtoList(queryDTO);
    }

    @Override
    public ReportLinkDetailResponse getLinkDetail(String traceId, Integer amdbReportTraceId) {
        // 时间解析 查询前后30分钟
        Long time = TraceIdUtil.getTraceIdTime(traceId);
        RpcStack rpcStack = traceClient.getTraceDetailById(traceId,
            DateUtils.dateToString(new Date(time - 1000 * 60 * 30), DateUtils.FORMATE_YMDHMS).replace(" ", "%20"),
            DateUtils.dateToString(new Date(time + 1000 * 60 * 30), DateUtils.FORMATE_YMDHMS).replace(" ", "%20"));

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
     * @param id      reportTrace Detail id
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

            // by 无涯 20211025
            reportTraceDetailDTO.setMethodName(rpcEntry.getMethodName());
            reportTraceDetailDTO.setLogType(rpcEntry.getLogType());
            reportTraceDetailDTO.setLogTypeName(TraceNodeLogTypeEnum.judgeTraceNodeLogType(rpcEntry.getLogType()).getDesc());
            reportTraceDetailDTO.setAsync(rpcEntry.isAsync());
            reportTraceDetailDTO.setAsyncName(rpcEntry.isAsync() ? TraceNodeAsyncEnum.ASYNC.getDesc() : TraceNodeAsyncEnum.SYNCHRONIZE.getDesc());
            reportTraceDetailDTO.setMiddlewareName(rpcEntry.getMiddlewareName());

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

    private PageInfo<ReportTraceDTO> getReportTraceDtoList(ReportTraceQueryDTO queryDTO) {
        // 查询场景下的业务活动信息
        Pair<List<Long>, List<String>> businessActivityPair = querySceneActivities(queryDTO);

        // entryList 获得
        List<EntranceRuleDTO> entranceList = this.getEntryListByBusinessActivityIds(businessActivityPair.getLeft());
        List<String> entrances = businessActivityPair.getRight();
        if (CollectionUtils.isNotEmpty(entrances)) {
            entranceList.addAll(entrances.stream().map(EntranceRuleDTO::new).collect(Collectors.toList()));
        }

        // 如果压测引擎任务Id不为空，替换reportId，现在大数据taskId对应的是压测引擎任务Id
        Long taskId = queryDTO.getTaskId();
        if (Objects.nonNull(taskId)) {
            queryDTO.setReportId(taskId);
        }
        TraceInfoQueryDTO traceInfoQueryDTO = new TraceInfoQueryDTO();
        BeanUtils.copyProperties(queryDTO, traceInfoQueryDTO);
        traceInfoQueryDTO.setEntranceRuleDTOS(entranceList);
        traceInfoQueryDTO.setPageNum(queryDTO.getRealCurrent());
        traceInfoQueryDTO.setQueryType(2);
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
        PageInfo<ReportTraceDTO> reportTraceDtoPageInfo = new PageInfo<>(collect);
        reportTraceDtoPageInfo.setTotal(entryTraceInfoDtoPagingList.getTotal());
        return reportTraceDtoPageInfo;
    }

    /**
     * 业务活动ids, 获得 entryList
     *
     * @param businessActivityIds 业务活动ids
     * @return entryList
     */
    @Override
    public List<EntranceRuleDTO> getEntryListByBusinessActivityIds(List<Long> businessActivityIds) {
        if (CollectionUtils.isEmpty(businessActivityIds)) {
            return Lists.newArrayList();
        }
        // 查询入口集合
        List<BusinessLinkResult> results = businessLinkManageDAO.getListByIds(businessActivityIds);
        List<EntranceRuleDTO> entranceList = Lists.newArrayList();
        for (BusinessLinkResult result : results) {
            EntranceRuleDTO dto = new EntranceRuleDTO();
            // 如果业务活动为：tohttp#GET, 则出现问题
            //String entrance = result.getEntrace();
            //if (ActivityUtil.isNormalBusiness(result.getType()) && entrance.contains("http")) {
            //    entrance = entrance.substring(entrance.indexOf("http"));
            //}
            dto.setEntrance(result.getEntrace());
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

    // 查询场景对应的业务活动Id
    private Pair<List<Long>, List<String>> querySceneActivities(ReportTraceQueryDTO queryDTO) {
        String xpath = queryDTO.getXpathMd5();
        List<Long> activityIds = new ArrayList<>();
        List<String> rule = new ArrayList<>();
        // 通过xpath递归node
        List<ScriptNodeTreeResp> nodeTree = reportApi.scriptNodeTree(
            new ScriptNodeTreeQueryReq() {{
                setSceneId(queryDTO.getSceneId());
                setReportId(queryDTO.getReportId());
            }});
        if (StringUtils.isBlank(xpath)) {
            SceneManageWrapperResp response = cloudSceneApi.getSceneDetail(new SceneManageIdReq() {{
                setId(queryDTO.getSceneId());
            }});
            List<SceneBusinessActivityRefResp> businessActivityConfig = response.getBusinessActivityConfig();
            activityIds.addAll(businessActivityConfig.stream().
                map(SceneBusinessActivityRefResp::getBusinessActivityId).collect(Collectors.toList()));
            recursionAllPath(nodeTree, rule);
        } else {
            boolean matchNext = true;
            for (ScriptNodeTreeResp node : nodeTree) {
                if (matchNext) {
                    matchNext = recursionMatchXpath(false, xpath, node, activityIds, rule);
                }
            }
        }
        return Pair.of(activityIds, rule);
    }

    /**
     * 递归查询对应xpath的所有子节点
     *
     * @param parentMatched 父节点是否匹配
     * @param xpath         xpathMd5
     * @param node          节点
     * @param result        结果集
     * @param rule          path结果集
     * @return 是否继续递归 true-继续
     */
    private boolean recursionMatchXpath(boolean parentMatched, String xpath, ScriptNodeTreeResp node,
        List<Long> result, List<String> rule) {
        String xpathMd5 = node.getXpathMd5();
        boolean curMatched = xpath.equals(xpathMd5);
        // md5为空，代表旧版本数据，此时identification也为空，同时没有子节点(即：不存在父节点匹配情况)
        Long businessActivityId = node.getBusinessActivityId();
        String identification = node.getIdentification();
        boolean identificationNotBlank = StringUtils.isNotBlank(identification);
        if (curMatched && (StringUtils.isBlank(node.getMd5()) || identificationNotBlank)) {
            if (Objects.nonNull(businessActivityId)) {
                result.add(businessActivityId);
                if (identificationNotBlank) {
                    rule.add(identification);
                }
            }
            return false;
        }
        if (parentMatched && identificationNotBlank) {
            if (Objects.nonNull(businessActivityId)) {
                result.add(businessActivityId);
                rule.add(identification);
            }
        }
        boolean matched = parentMatched || curMatched;
        List<ScriptNodeTreeResp> children = node.getChildren();
        boolean matchNext = true;
        if (CollectionUtils.isNotEmpty(children)) {
            for (ScriptNodeTreeResp child : children) {
                if (matchNext) {
                    matchNext = recursionMatchXpath(matched, xpath, child, result, rule);
                }
            }
        }
        return !curMatched && matchNext;
    }

    private void recursionAllPath(List<ScriptNodeTreeResp> nodeTree, List<String> rule) {
        nodeTree.forEach(node -> {
            String identification = node.getIdentification();
            if (StringUtils.isNotBlank(identification)) {
                rule.add(identification);
            }
            List<ScriptNodeTreeResp> children = node.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                recursionAllPath(children, rule);
            }
        });
    }
}
