package io.shulie.takin.web.biz.service.report.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneBusinessActivityRefMapper;
import com.pamirs.takin.entity.domain.dto.report.*;
import com.pamirs.takin.entity.domain.entity.report.TpsTargetArray;
import io.shulie.takin.adapter.api.model.request.report.ReportCostTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.common.pojo.Pair;
import io.shulie.takin.cloud.common.utils.TestTimeUtil;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceMetricsRequest;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.amdb.bean.result.trace.TraceMetrics;
import io.shulie.takin.web.amdb.enums.LinkRequestResultTypeEnum;
import io.shulie.takin.web.biz.pojo.input.report.NodeCompareTargetInput;
import io.shulie.takin.web.biz.pojo.output.report.*;
import io.shulie.takin.web.biz.pojo.request.activity.ReportActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ReportActivityResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.report.ReportLocalService;
import io.shulie.takin.web.biz.service.report.ReportMessageService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ReportConfigConstant;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
import io.shulie.takin.web.data.dao.report.ReportApplicationSummaryDAO;
import io.shulie.takin.web.data.dao.report.ReportBottleneckInterfaceDAO;
import io.shulie.takin.web.data.dao.report.ReportMachineDAO;
import io.shulie.takin.web.data.dao.report.ReportSummaryDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.report.ReportApplicationSummaryQueryParam;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.result.report.ReportApplicationSummaryResult;
import io.shulie.takin.web.data.result.report.ReportBottleneckInterfaceResult;
import io.shulie.takin.web.data.result.report.ReportMachineResult;
import io.shulie.takin.web.data.result.report.ReportSummaryResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/7/27 下午8:39
 */
@Service
@Slf4j
public class ReportLocalServiceImpl implements ReportLocalService {

    public static final BigDecimal ZERO = new BigDecimal("0");
    @Autowired
    private ReportSummaryDAO reportSummaryDAO;
    @Resource
    private ReportBottleneckInterfaceDAO reportBottleneckInterfaceDAO;
    @Resource
    private ReportApplicationSummaryDAO reportApplicationSummaryDAO;
    @Resource
    private ReportMachineDAO reportMachineDAO;

    @Autowired
    private ReportRealTimeService reportRealTimeService;

    @Resource
    private ReportService reportService;

    @Resource
    private ReportMessageService reportMessageService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private TraceClient traceClient;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private ApplicationMntMapper applicationMntMapper;

    @Resource
    private TSceneBusinessActivityRefMapper tSceneBusinessActivityRefMapper;

    public static void main(String[] args) {
        String data1
                = "{\"cpu\":[10,11,12],\"io\":[40,30,35],\"loading\":[75,55,70],\"memory\":[40,43,45],\"network\":[20,40,"
                + "24],\"tps\":[100,110,120]}";
        String data2
                = "{\"cpu\":[30,31,32],\"io\":[42,32,37],\"loading\":[77,57,72],\"memory\":[42,45,47],\"network\":[22,42,"
                + "26],\"tps\":[110,120,130]}";
        MachineDetailDTO dto = new MachineDetailDTO();
        ReportLocalServiceImpl reportLocal = new ReportLocalServiceImpl();
        reportLocal.parseTpsConfig(dto, Arrays.asList(data1, data2));
        System.out.println(dto);
    }

    @Override
    public ReportCountDTO getReportCount(Long reportId) {
        ReportSummaryResult data = reportSummaryDAO.selectOneByReportId(reportId);
        if (data == null) {
            return new ReportCountDTO();
        }
        return convert2ReportCountDTO(data);
    }

    @Override
    public PageInfo<BottleneckInterfaceDTO> listBottleneckInterface(ReportLocalQueryParam queryParam) {
        Page page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        List<ReportBottleneckInterfaceResult> dataList = reportBottleneckInterfaceDAO.selectByExample(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<BottleneckInterfaceDTO> resultList = convert2BottleneckInterfaceDTO(dataList);
        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public RiskApplicationCountDTO listRiskApplication(Long reportId) {
        ReportApplicationSummaryQueryParam param = new ReportApplicationSummaryQueryParam();
        param.setReportId(reportId);
        List<ReportApplicationSummaryResult> dataList = reportApplicationSummaryDAO.selectByParam(param);
        if (CollectionUtils.isEmpty(dataList)) {
            return new RiskApplicationCountDTO();
        }
        List<ReportApplicationSummaryResult> resultList = dataList.stream().filter(
                data -> data.getMachineRiskCount() != null && data.getMachineRiskCount() > 0).sorted((o1, o2) -> {
            if (o1.getMachineRiskCount() > o2.getMachineRiskCount()) {
                return -1;
            } else if (o1.getMachineRiskCount() < o2.getMachineRiskCount()) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(resultList)) {
            return new RiskApplicationCountDTO();
        }
        //风险机器数从汇总表里取
        int riskMachineCount = 0;
        ReportSummaryResult data = reportSummaryDAO.selectOneByReportId(reportId);
        if (data != null) {
            riskMachineCount = data.getRiskMachineCount();
        }
        return convert2RiskApplicationCountDTO(resultList, riskMachineCount);
    }

    @Override
    public PageInfo<RiskMacheineDTO> listRiskMachine(ReportLocalQueryParam queryParam) {
        Page page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        queryParam.setRiskFlag(1);
        List<ReportMachineResult> dataList = reportMachineDAO.selectSimpleByExample(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<RiskMacheineDTO> resultList = convert2RiskMacheineDTO(dataList);
        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public MachineDetailDTO getMachineDetail(Long reportId, String applicationName, String machineIp) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        queryParam.setApplicationName(applicationName);
        queryParam.setMachineIp(machineIp);
        if ("全部".equals(machineIp) || "0".equals(machineIp) || "all".equalsIgnoreCase(machineIp)) {
            //只展示图表
            queryParam.setMachineIp(null);
            List<ReportMachineResult> dataList = reportMachineDAO.selectListByParam(queryParam);
            if (CollectionUtils.isEmpty(dataList)) {
                return new MachineDetailDTO();
            }
            MachineDetailDTO dto = new MachineDetailDTO();
            parseTpsConfig(dto, dataList.stream().filter(data -> data.getMachineTpsTargetConfig() != null)
                    .map(ReportMachineResult::getMachineTpsTargetConfig).collect(Collectors.toList()));
            return dto;
        } else {
            ReportMachineResult data = reportMachineDAO.selectOneByParam(queryParam);
            if (data == null) {
                return new MachineDetailDTO();
            }
            return convert2MachineDetailDTO(data);
        }
    }

    @Override
    public List<ApplicationDTO> listApplication(Long reportId, String applicationName) {
        ReportApplicationSummaryQueryParam param = new ReportApplicationSummaryQueryParam();
        param.setReportId(reportId);
        param.setApplicationName(applicationName);
        List<ReportApplicationSummaryResult> dataList = reportApplicationSummaryDAO.selectByParam(param);
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        return convert2ApplicationDTO(dataList);
    }

    @Override
    public PageInfo<MachineDetailDTO> listMachineDetail(ReportLocalQueryParam queryParam) {
        Page page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        List<ReportMachineResult> dataList = reportMachineDAO.selectSimpleByExample(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<MachineDetailDTO> resultList = convert2MachineDetailDTO(dataList);
        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    //@Override
    //public Long getTraceFailedCount(Long reportId) {
    //    WebResponse reportByReportId = null;
    //    try {
    //        reportByReportId = reportService.getReportByReportId(reportId);
    //    } catch (Exception e) {
    //        return 0L;
    //    }
    //    if (!reportByReportId.getSuccess()) {
    //        return 0L;
    //    }
    //    LinkedHashMap map = (LinkedHashMap)reportByReportId.getData();
    //    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
    //    Long endTime = (Long)map.get("endTime");
    //    Long startTime;
    //    try {
    //        startTime = simpleDateFormat.parse((String)map.get("startTime")).getTime();
    //    } catch (ParseException e) {
    //        // 如果 startTime 有问题，那么最多取10分钟
    //        startTime = endTime - 10 * 60 * 1000;
    //    }
    //    return TReportMachineMapper.getTraceFailedCount(startTime, endTime);
    //}

    @Override
    public Long getTraceFailedCount(Long reportId) {
        ReportTraceQueryDTO queryDTO = new ReportTraceQueryDTO();
        queryDTO.setQueryType(2);
        queryDTO.setReportId(reportId);
        queryDTO.setPageSize(1);
        queryDTO.setResultType(LinkRequestResultTypeEnum.FAILED.getCode());
        PageInfo<ReportTraceDTO> failed = reportRealTimeService.getReportLinkListByReportId(queryDTO);
        queryDTO.setResultType(LinkRequestResultTypeEnum.FAILED_ASSERT.getCode());
        queryDTO.setReportId(reportId);
        PageInfo<ReportTraceDTO> failedAssert = reportRealTimeService.getReportLinkListByReportId(queryDTO);

        long failedTotal = 0;
        long failedAssertTotal = 0;
        if (failed != null) {
            failedTotal = failed.getTotal();
        }

        if (failedAssert != null) {
            failedAssertTotal = failedAssert.getTotal();
        }

        return failedTotal + failedAssertTotal;
    }

    @Override
    public ReportCompareOutput getReportCompare(List<Long> reportIds, Long businessActivityId) {
        ReportCompareOutput output = new ReportCompareOutput();
        //查询业务活动serviceName和methodName
        ActivityResponse activityResp = activityService.getActivityServiceById(businessActivityId);
        if (activityResp == null) {
            return output;
        }
        Map<Long, ReportDetailOutput> reportOutputMap = new HashMap<>();
        Integer minRt = 0;
        Integer maxRt = 0;
        String bindRef = null;
        for (int i = 0; i < reportIds.size(); i++) {
            Long reportId = reportIds.get(i);
            ReportDetailOutput reportOutput = reportService.getReportByReportId(reportId);
            if (reportOutput == null) {
                continue;
            }
            //柱状图数据
            ReportCompareColumnarOut columnarOut = new ReportCompareColumnarOut();
            columnarOut.setReportId(reportId);
            columnarOut.setTps(reportOutput.getAvgTps());
            columnarOut.setRt(reportOutput.getAvgRt());
            columnarOut.setSuccessRate(reportOutput.getSuccessRate());
            output.getColumnarData().add(columnarOut);
            BusinessActivitySummaryBean detailEntity = reportOutput.getBusinessActivity().stream().filter(data -> data.getBusinessActivityId().longValue() == businessActivityId.longValue()).findFirst().orElse(null);
            if (detailEntity == null || detailEntity.getBindRef() == null || StringUtils.isBlank(detailEntity.getRtDistribute())) {
                continue;
            }
            reportOutputMap.put(reportId, reportOutput);
            bindRef = detailEntity.getBindRef();
            //列表数据 性能指标 rt数据
            JSONObject jsonObject = JSON.parseObject(detailEntity.getRtDistribute());
            String jsonStepString = jsonObject.getString("stepData");
            List<ReportCompareTargetOut> targetList = JsonHelper.json2List(jsonStepString, ReportCompareTargetOut.class);
            if (CollectionUtils.isNotEmpty(targetList)) {
                targetList.stream().forEach(data -> {
                    data.setReportId(reportId);
                    data.setPressureTestTime(TestTimeUtil.format(DateUtil.parseDateTime(data.getStartTime()), DateUtil.parseDateTime(data.getEndTime())));
                });
                output.getTargetData().addAll(targetList);
            }
            List<ReportCompareRtOutput> rtList = JsonHelper.json2List(jsonStepString, ReportCompareRtOutput.class);
            if (CollectionUtils.isNotEmpty(rtList)) {
                rtList.stream().forEach(data -> {
                    data.setReportId(reportId);
                    data.setPressureTestTime(TestTimeUtil.format(DateUtil.parseDateTime(data.getStartTime()), DateUtil.parseDateTime(data.getEndTime())));
                });
                output.getRtData().addAll(rtList);
            }
            //趋势图数据 TPS RT
            ReportTrendQueryReq trendQueryReq = new ReportTrendQueryReq();
            trendQueryReq.setReportId(reportId);
            trendQueryReq.setXpathMd5(detailEntity.getBindRef());
            ReportTrendResp trendResp = reportService.queryReportTrend(trendQueryReq);
            ReportCompareTrendOut trendOut = new ReportCompareTrendOut();
            if (trendResp != null) {
                trendOut.setReportId(reportId);
                trendOut.setXTime(trendResp.getTime());
                trendOut.setConcurrent(trendResp.getConcurrent());
                trendOut.setTps(trendResp.getTps());
                trendOut.setRt(trendResp.getRt());
                trendOut.setSuccessRate(trendResp.getSuccessRate());
                trendOut.setSa(trendResp.getSa());
                Pair<Integer, Integer> pair = getMinMaxRt(trendResp.getRt());
                if (pair != null) {
                    if (minRt == 0) {
                        minRt = pair.getKey();
                    } else {
                        minRt = Math.min(minRt, pair.getKey());
                    }
                    maxRt = Math.max(maxRt, pair.getValue());
                }
            }
            output.getTrendData().add(trendOut);
        }
        List<Pair<Integer, Integer>> costList = calcCostLevelByFive(minRt, maxRt);
        //按耗时取请求量
        for (int i = 0; i < reportIds.size(); i++) {
            Long reportId = reportIds.get(i);
            ReportDetailOutput reportOutput = reportOutputMap.get(reportId);
            if (reportOutput == null || CollectionUtils.isEmpty(costList)) {
                continue;
            }
            for (Pair<Integer, Integer> costPair : costList) {
                ReportCostTrendQueryReq costReq = new ReportCostTrendQueryReq();
                costReq.setStartTime(DateUtil.parseDateTime(reportOutput.getStartTime()).getTime());
                costReq.setEndTime(reportOutput.getEndTime().getTime());
                costReq.setJobId(reportOutput.getJobId());
                costReq.setServiceName(activityResp.getServiceName());
                costReq.setRequestMethod(activityResp.getMethod());
                costReq.setMinCost(costPair.getKey());
                costReq.setMaxCost(costPair.getValue());
                costReq.setTransaction(bindRef);
                Long costCount = reportMessageService.getRequestCountByCost(costReq);
                output.getTrendData().get(i).getXCost().add(costPair.getKey() + "-" + costPair.getValue() + "ms");
                output.getTrendData().get(i).getCount().add(String.valueOf(costCount));
            }
        }
        return output;
    }


    //压测报告节点rt比较
    @Override
    public Response<NodeCompareTargetOut> getLtNodeCompare(NodeCompareTargetInput nodeCompareTargetInput) {
        //获取报告信息
        List<ReportEntity> reportEntityList = this.reportService.getReportListByReportIds(nodeCompareTargetInput.getReportIds());
        if (CollectionUtils.isEmpty(reportEntityList)) {
            return Response.fail("报告不存在");
        }
        //根据业务活动id获取节点信息
        List<ReportActivityInfoQueryRequest> activityInfoQueryRequests = reportEntityList.stream().map(reportEntity -> genActivityInfo(nodeCompareTargetInput.getActivityId(), reportEntity)).collect(Collectors.toList());
        //根据业务活动id，报告id获取压测时候节点的信息
        if (CollectionUtils.isEmpty(activityInfoQueryRequests)) {
            return Response.success(new NodeCompareTargetOut());
        }

        List<ReportActivityResponse> activityResponseList = this.activityService.getActivityWithMetricsByIdForReports(activityInfoQueryRequests);
        //统计转换压测时候节点的信息
        if (CollectionUtils.isEmpty(activityResponseList)) {
            return Response.success(new NodeCompareTargetOut());
        }
        NodeCompareTargetOut nodeCompareTargetOut = new NodeCompareTargetOut();
        nodeCompareTargetOut.setReportIds(nodeCompareTargetInput.getReportIds());
        NodeCompareTargetOut.TopologyNode root = new NodeCompareTargetOut.TopologyNode();
        ReportActivityResponse response = activityResponseList.get(0);
        root.setId(response.getLinkId());
        root.setMethodName(response.getMethod());
        root.setService(response.getServiceName());
        root.setLabel(response.getApplicationName());
        Map<String,NodeCompareTargetOut.TopologyNode> topologyNode = genNodeCompareTargetOut(activityResponseList);
        nodeCompareTargetOut.setNode(genNodeTree(root,topologyNode));
        return Response.success(nodeCompareTargetOut);
    }

    private static Map<String, NodeCompareTargetOut.TopologyNode> genNodeCompareTargetOut(List<ReportActivityResponse> activityResponseList) {
        NodeCompareTargetOut.TopologyNode topologyNode = new NodeCompareTargetOut.TopologyNode();
        List<Map<String, NodeCompareTargetOut.TopologyNode>> list = new ArrayList<>();
        for (ReportActivityResponse activityResponse : activityResponseList) {
            topologyNode.setId(activityResponse.getLinkId());
            topologyNode.setMethodName(activityResponse.getMethod());
            topologyNode.setService(activityResponse.getServiceName());
            topologyNode.setLabel(activityResponse.getApplicationName());
            Map<String, NodeCompareTargetOut.TopologyNode> map = new HashMap<>();
            if (activityResponse.getTopology() != null && CollectionUtils.isNotEmpty(activityResponse.getTopology().getNodes())) {
                for (ApplicationEntranceTopologyResponse.AbstractTopologyNodeResponse node : activityResponse.getTopology().getNodes()) {
                    NodeCompareTargetOut.TopologyNode topologyNodeTree = new NodeCompareTargetOut.TopologyNode();
                    topologyNodeTree.setId(node.getId());
                    topologyNodeTree.setLabel(node.getLabel());
                    topologyNodeTree.setService1Rt(node.getServiceRt());
                    topologyNodeTree.setService(activityResponse.getServiceName());
                    topologyNodeTree.setMethodName(activityResponse.getMethod());
                    if (CollectionUtils.isEmpty(node.getUpAppNames())) {
                        map.put(node.getLabel(), topologyNodeTree);
                    }
                    for (String upAppName : node.getUpAppNames()) {
                        map.put(node.getLabel() + "&&&&&&" + upAppName, topologyNodeTree);
                    }
                }
            }
            list.add(map);
        }
        //合并数据
        Set<String> keys1 = list.get(0).keySet();
        Map<String, NodeCompareTargetOut.TopologyNode> topologyNodeMap1 = list.get(0);
        Map<String, NodeCompareTargetOut.TopologyNode> topologyNodeMap2 = list.get(1);
        Map<String, NodeCompareTargetOut.TopologyNode> newNodeMap = new HashMap<>();
        for (String s : keys1) {
            NodeCompareTargetOut.TopologyNode topologyNode1 = topologyNodeMap1.get(s);
            NodeCompareTargetOut.TopologyNode topologyNode2 = topologyNodeMap2.get(s);
            if (topologyNode1 == null || topologyNode2 == null) {
                continue;
            }
            topologyNode1.setService2Rt(topologyNode2.getService1Rt());
            newNodeMap.put(s, topologyNode1);
        }
        return newNodeMap;
    }

    private static NodeCompareTargetOut.TopologyNode genNodeTree(NodeCompareTargetOut.TopologyNode root, Map<String, NodeCompareTargetOut.TopologyNode> map) {
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String[] split = key.split("&&&&&&");
            if (split.length != 2) {
                continue;
            }
            if (root.getLabel().equals(split[1])) {
                if (CollectionUtils.isEmpty(root.getNodes())) {
                    List<NodeCompareTargetOut.TopologyNode> list = new ArrayList<>();
                    list.add(map.get(key));
                    root.setNodes(list);
                } else {
                    root.getNodes().add(map.get(key));
                }
                genNodeTree(map.get(key), map);
            }
        }
        return root;
    }

    /**
     * 获取报告应用性能列表
     *
     * @return
     */
    @Override
    public Response<List<ReportAppPerformanceOut>> getReortAppPerformanceList(Long reportId) {
        ReportEntity reportEntity = getReportEntity(reportId);
        if (reportEntity == null) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<SceneBusinessActivityRefEntity> sceneBusinessActivityRefEntities = getSceneBusinessActivityRefEntities(reportEntity.getSceneId());

        if (CollectionUtils.isEmpty(sceneBusinessActivityRefEntities)) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<ReportActivityInfoQueryRequest> activityInfoQueryRequests = sceneBusinessActivityRefEntities.stream().map(sceneBusinessActivityRef -> genActivityInfo(sceneBusinessActivityRef.getBusinessActivityId(), reportEntity)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(activityInfoQueryRequests)) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<ReportActivityResponse> activityResponseList = this.activityService.getActivityWithMetricsByIdForReports(activityInfoQueryRequests);

        if (CollectionUtils.isEmpty(activityResponseList)) {
            return Response.success(Collections.EMPTY_LIST);
        }

        ReportDetailOutput reportDetailOutput = reportService.getReportByReportId(reportId);
        List<ReportAppPerformanceOut> list = activityResponseList.get(0).getTopology().getNodes().stream().filter(data -> data.getNodeType() == ApplicationEntranceTopologyResponse.NodeTypeResponseEnum.APP).map(node -> {
            ReportAppPerformanceOut reportAppPerformanceOut = new ReportAppPerformanceOut();
            reportAppPerformanceOut.setAppName(node.getLabel());
            reportAppPerformanceOut.setTotalRequest(node.getServiceAllTotalCount() != null ? formatBigDecimal(node.getServiceAllTotalCount()) : new BigDecimal(0));
            reportAppPerformanceOut.setAvgTps(node.getServiceAllTotalTps() != null ? formatBigDecimal(node.getServiceAllTotalTps()) : new BigDecimal(0));
            reportAppPerformanceOut.setAvgRt(node.getServiceRt() != null ? formatBigDecimal(node.getServiceRt()) : new BigDecimal(0));
            reportAppPerformanceOut.setMaxRt(node.getServiceMaxRt() != null ? formatBigDecimal(node.getServiceMaxRt()) : new BigDecimal(0));
            reportAppPerformanceOut.setMinRt(node.getServiceMinRt() != null ? formatBigDecimal(node.getServiceMinRt()) : new BigDecimal(0));
            reportAppPerformanceOut.setSuccessRate(formatBigDecimalPercent(node.getServiceAllSuccessRate() != null ? formatBigDecimal(node.getServiceAllSuccessRate()) : new BigDecimal(0)));
            reportAppPerformanceOut.setSa(reportDetailOutput.getSa());
            reportAppPerformanceOut.setStartTime(DateUtil.formatDateTime(reportEntity.getStartTime()));
            return reportAppPerformanceOut;
        }).collect(Collectors.toList());
        return Response.success(list);
    }

    /**
     * 获取报告应用实例性能列表
     *
     * @param reportId
     * @return
     */
    @Override
    public Response<List<ReportAppInstancePerformanceOut>> getReortAppInstancePerformanceList(Long reportId) {
        ReportEntity reportEntity = getReportEntity(reportId);
        if (reportEntity == null) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<SceneBusinessActivityRefEntity> sceneBusinessActivityRefEntities = getSceneBusinessActivityRefEntities(reportEntity.getSceneId());

        if (CollectionUtils.isEmpty(sceneBusinessActivityRefEntities)) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<Long> appIds = sceneBusinessActivityRefEntities.stream().map(SceneBusinessActivityRefEntity::getApplicationIds).filter(StringUtils::isNotBlank).flatMap(s -> Arrays.stream(s.split(",")).map(Long::valueOf)).distinct().collect(Collectors.toList());

        List<ApplicationMntEntity> applicationMntEntities = applicationMntMapper.selectList(new LambdaQueryWrapper<ApplicationMntEntity>().select(ApplicationMntEntity::getApplicationName).in(ApplicationMntEntity::getApplicationId, appIds));

        if (CollectionUtils.isEmpty(applicationMntEntities)) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<MachineDetailDTO> machineDetailDTOList = new ArrayList<>();
        for (ApplicationMntEntity applicationMntEntity : applicationMntEntities) {
            ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
            queryParam.setReportId(reportId);
            queryParam.setApplicationName(applicationMntEntity.getApplicationName());
            queryParam.setCurrent(0);
            queryParam.setPageSize(9999);
            PageInfo<MachineDetailDTO> machineDetailDTOPageInfo = listMachineDetail(queryParam);
            if (CollectionUtils.isEmpty(machineDetailDTOPageInfo.getList())) {
                continue;
            }
            for (MachineDetailDTO machineDetailDTO : machineDetailDTOPageInfo.getList()) {
                MachineDetailDTO machineDetail = getMachineDetail(reportId, machineDetailDTO.getApplicationName(), machineDetailDTO.getMachineIp());
                if (Objects.isNull(machineDetail)) {
                    continue;
                }
                machineDetailDTOList.add(machineDetail);
            }
        }
        List<ReportAppInstancePerformanceOut> reportAppInstancePerformanceOuts = machineDetailDTOList.stream().filter(Objects::nonNull).map(machine -> {
            ReportAppInstancePerformanceOut reportAppInstancePerformanceOut = new ReportAppInstancePerformanceOut();
            reportAppInstancePerformanceOut.setAppName(machine.getApplicationName());
            reportAppInstancePerformanceOut.setInstanceName(machine.getAgentId());
            reportAppInstancePerformanceOut.setAvgCpuUsageRate(getAvg(Arrays.asList(machine.getTpsTarget().getCpu())));
            reportAppInstancePerformanceOut.setAvgMemUsageRate(getAvg(Arrays.asList(machine.getTpsTarget().getMemory())));
            reportAppInstancePerformanceOut.setAvgDiskIoWaitRate(getAvg(Arrays.asList(machine.getTpsTarget().getIo())));
            reportAppInstancePerformanceOut.setAvgNetUsageRate(getAvg(Arrays.asList(machine.getTpsTarget().getMbps())));
            if(machine.getTpsTarget().getGcCount() != null && machine.getTpsTarget().getGcCount().length > 0){
                reportAppInstancePerformanceOut.setGcCount(Arrays.stream(machine.getTpsTarget().getGcCount()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).intValue());
            } else {
                reportAppInstancePerformanceOut.setGcCount(0);
            }
            if(machine.getTpsTarget().getGcCost() != null && machine.getTpsTarget().getGcCost().length > 0){
                reportAppInstancePerformanceOut.setGcCost(Arrays.stream(machine.getTpsTarget().getGcCost()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
            } else {
                reportAppInstancePerformanceOut.setGcCost(BigDecimal.ZERO);
            }
            reportAppInstancePerformanceOut.setAvgTps(getAvg(Arrays.stream(machine.getTpsTarget().getTps()).map(BigDecimal::valueOf).collect(Collectors.toList())));
            return reportAppInstancePerformanceOut;
        }).collect(Collectors.toList());
        return Response.success(reportAppInstancePerformanceOuts);
    }

    private BigDecimal getAvg(List<BigDecimal> num) {
        BigDecimal total = BigDecimal.valueOf(num.size());
        BigDecimal count = num.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        if (num.size() == 0) {
            return new BigDecimal(0);
        }
        return count.divide(total, 2, RoundingMode.HALF_UP);
    }

    private List<SceneBusinessActivityRefEntity> getSceneBusinessActivityRefEntities(Long sceneId) {
        return tSceneBusinessActivityRefMapper.selectList(new LambdaQueryWrapper<SceneBusinessActivityRefEntity>().eq(SceneBusinessActivityRefEntity::getSceneId, sceneId).select(SceneBusinessActivityRefEntity::getBusinessActivityId, SceneBusinessActivityRefEntity::getApplicationIds));
    }

    /**
     * 获取报告应用性能趋势图
     *
     * @return
     */
    @Override
    public Response<List<ReportAppMapOut>> getReportAppTrendMap(Long reportId) {
        TenantCommonExt tenantCommonExt = WebPluginUtils.traceTenantCommonExt();
        UserExt userExt = WebPluginUtils.traceUser();
        ReportEntity reportEntity = getReportEntity(reportId);
        if (reportEntity == null) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<SceneBusinessActivityRefEntity> sceneBusinessActivityRefEntities = getSceneBusinessActivityRefEntities(reportEntity.getSceneId());

        if (CollectionUtils.isEmpty(sceneBusinessActivityRefEntities)) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<Long> appIds = sceneBusinessActivityRefEntities.stream().map(SceneBusinessActivityRefEntity::getApplicationIds).filter(StringUtils::isNotBlank).flatMap(s -> Arrays.stream(s.split(",")).map(Long::valueOf)).collect(Collectors.toList());

        List<ApplicationMntEntity> applicationMntEntities = applicationMntMapper.selectList(new LambdaQueryWrapper<ApplicationMntEntity>().select(ApplicationMntEntity::getApplicationName).eq(ApplicationMntEntity::getApplicationId, appIds));
        List<String> appNames = applicationMntEntities.stream().map(ApplicationMntEntity::getApplicationName).collect(Collectors.toList());
        TraceMetricsRequest traceMetricsRequest = new TraceMetricsRequest();
        traceMetricsRequest.setStartTime(reportEntity.getStartTime().getTime());
        traceMetricsRequest.setEndTime(reportEntity.getEndTime().getTime());
        traceMetricsRequest.setClusterTest(1);
        traceMetricsRequest.setQuerySource("tro");
        traceMetricsRequest.setUserId(String.valueOf(userExt.getId()));
        traceMetricsRequest.setUserName(userExt.getName());
        traceMetricsRequest.setTenantAppKey(tenantCommonExt.getTenantAppKey());
        traceMetricsRequest.setEnvCode(tenantCommonExt.getEnvCode());
        traceMetricsRequest.setAppNames(appNames);
        List<TraceMetrics> metricsList = traceClient.getSqlStatements(traceMetricsRequest);
        if (CollectionUtils.isEmpty(metricsList)) {
            return Response.success(Collections.EMPTY_LIST);
        }

        Map<String, List<TraceMetrics>> map = metricsList.stream().collect(Collectors.groupingBy(TraceMetrics::getAppName));
        //根据map分别计算tps趋势图、成功率趋势图、rt趋势图
        List<ReportAppMapOut> reportAppMapOuts = new ArrayList<>(map.size());
        map.forEach((k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                return;
            }
            List<Double> tps = new ArrayList<>(v.size());
            List<Double> rt = new ArrayList<>(v.size());
            List<Double> successRate = new ArrayList<>(v.size());
            List<Integer> totalRequest = new ArrayList<>(v.size());
            v.stream().sorted(Comparator.comparing(TraceMetrics::getTime)).forEach(traceMetrics -> {
                tps.add(BigDecimal.valueOf(traceMetrics.getAvgTps()).doubleValue());
                rt.add(BigDecimal.valueOf(traceMetrics.getAvgRt()).doubleValue());
                totalRequest.add(traceMetrics.getTotal());
                double suRate = BigDecimal.valueOf(traceMetrics.getSuccessCount()).divide(BigDecimal.valueOf(traceMetrics.getTotal()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                successRate.add(suRate);
            });

            //计算最大值
            Integer max = v.stream().map(TraceMetrics::getAvgRt).max(Double::compare).get();
            //计算最小值
            Integer min = v.stream().map(TraceMetrics::getAvgRt).min(Integer::compare).get();
            List<String> intervalList = getInterval(min, max, 5);
            LinkedHashMap<String, Integer> timeAndRequestMap = new LinkedHashMap<>();

            for (String inter : intervalList) {
                String str[] = inter.split("-");
                Integer count = v.stream().filter(traceMetrics -> traceMetrics.getAvgRt() >= Integer.valueOf(str[0]) && traceMetrics.getAvgRt() < Integer.valueOf(str[1])).map(TraceMetrics::getTotal).reduce(Integer::sum).orElse(0);
                timeAndRequestMap.put(inter, count);
            }
            ReportAppMapOut reportAppMapOut = new ReportAppMapOut();
            reportAppMapOut.setAppName(k);
            reportAppMapOut.setTps(tps.toArray(new Double[0]));
            reportAppMapOut.setRt(rt.toArray(new Double[0]));
            reportAppMapOut.setSuccessRate(successRate.toArray(new Double[0]));
            reportAppMapOut.setTotalRequest(totalRequest.toArray(new Integer[0]));
            timeAndRequestMap.put("0", 0);
            reportAppMapOut.setTimeAndRequestMap(timeAndRequestMap);
            reportAppMapOuts.add(reportAppMapOut);
        });
        return Response.success(reportAppMapOuts);
    }

    /**
     * 获取报告应用实例性能趋势图
     *
     * @param reportId
     * @return
     */
    @Override
    public Response<List<MachineDetailDTO>> getReportAppInstanceTrendMap(Long reportId) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        List<ReportMachineResult> dataList = reportMachineDAO.selectListByParam(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return Response.success(Collections.EMPTY_LIST);
        }
        List<MachineDetailDTO> machineDetailDTOList = new ArrayList<>();
        for(ReportMachineResult result : dataList) {
            machineDetailDTOList.add(convert2MachineDetailDTO(result));
        }
        machineDetailDTOList.sort(Comparator.comparing(MachineDetailDTO::getApplicationName).thenComparing(MachineDetailDTO::getProcessName));
        return Response.success(machineDetailDTOList);
    }

    /**
     * 从rt的最大值和最小值中取出5个区间
     *
     * @param start    最小值
     * @param end      最大值
     * @param interval 区间间隔
     * @return
     */
    private static List<String> getInterval(int start, int end, int interval) {
        if (start >= end || interval == 0) {
            return Collections.EMPTY_LIST;
        }
        double intervalNum = BigDecimal.valueOf(end).subtract(BigDecimal.valueOf(start)).divide(BigDecimal.valueOf(interval)).doubleValue();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int startNum = (int) (start + i * intervalNum);
            int endNum = (int) (start + (i + 1) * intervalNum);
            list.add(startNum + "-" + endNum);
        }
        return list;
    }

    private ReportEntity getReportEntity(long reportId) {
        return reportMapper.selectOne(new LambdaQueryWrapper<ReportEntity>().eq(ReportEntity::getId, reportId).eq(ReportEntity::getIsDeleted, 0).select(ReportEntity::getId, ReportEntity::getSceneName, ReportEntity::getSceneId, ReportEntity::getEndTime, ReportEntity::getStartTime));
    }

    private static ReportActivityInfoQueryRequest genActivityInfo(long activityId, ReportEntity reportEntity) {
        ReportActivityInfoQueryRequest request = new ReportActivityInfoQueryRequest();
        request.setActivityId(activityId);
        //request.setFlowTypeEnum(FlowTypeEnum.PRESSURE_MEASUREMENT);
        request.setStartTime(reportEntity.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        request.setEndTime(reportEntity.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        request.setReportId(reportEntity.getId());
        return request;
    }

    private Pair<Integer, Integer> getMinMaxRt(List<String> rtList) {
        if (CollectionUtils.isEmpty(rtList)) {
            return null;
        }
        Pair<Integer, Integer> pair = new Pair<>();
        List<Integer> intList = new ArrayList<>();
        for (String rt : rtList) {
            intList.add(new BigDecimal(rt).intValue());
        }
        Collections.sort(intList);
        pair.setKey(intList.get(0));
        pair.setValue(intList.get(intList.size() - 1) + 1);
        return pair;
    }

    public List<Pair<Integer, Integer>> calcCostLevelByFive(Integer minRt, Integer maxRt) {
        log.info("calcCostLevelByFive, minRT={}, maxRt={}", minRt, maxRt);
        List<Pair<Integer, Integer>> pairList = new ArrayList<>();
        if (minRt == maxRt) {
            return pairList;
        }
        int step = Math.max(1, ((maxRt - minRt) / 5));
        for (int i = 0; i < 5; i++) {
            if (i == 4) {
                pairList.add(new Pair<>(minRt, maxRt));
            } else {
                if (minRt + step >= maxRt) {
                    pairList.add(new Pair<>(minRt, maxRt));
                    break;
                }
                pairList.add(new Pair<>(minRt, minRt + step));
                minRt += step;
            }
        }
        return pairList;
    }

    private ReportCountDTO convert2ReportCountDTO(ReportSummaryResult result) {
        ReportCountDTO dto = new ReportCountDTO();
        dto.setBottleneckInterfaceCount(result.getBottleneckInterfaceCount());
        dto.setRiskMachineCount(result.getRiskMachineCount());
        dto.setBusinessActivityCount(result.getBusinessActivityCount());
        dto.setNotpassBusinessActivityCount(result.getUnachieveBusinessActivityCount());
        dto.setApplicationCount(result.getApplicationCount());
        dto.setMachineCount(result.getMachineCount());
        dto.setWarnCount(result.getWarnCount());
        return dto;
    }

    private List<BottleneckInterfaceDTO> convert2BottleneckInterfaceDTO(List<ReportBottleneckInterfaceResult> results) {
        List<BottleneckInterfaceDTO> dataList = Lists.newArrayList();
        results.forEach(data -> {
            BottleneckInterfaceDTO dto = new BottleneckInterfaceDTO();
            dto.setRank(data.getSortNo());
            dto.setApplicationName(data.getApplicationName());
            dto.setInterfaceName(data.getInterfaceName());
            dto.setTps(data.getTps());
            dto.setRt(data.getRt());
            dto.setSuccessRate(new BigDecimal("100"));
            dataList.add(dto);
        });
        return dataList;
    }

    private RiskApplicationCountDTO convert2RiskApplicationCountDTO(List<ReportApplicationSummaryResult> paramList,
                                                                    int riskMachineCount) {
        RiskApplicationCountDTO dto = new RiskApplicationCountDTO();
        List<ApplicationDTO> apps = Lists.newArrayList();
        for (ReportApplicationSummaryResult param : paramList) {
            ApplicationDTO app = new ApplicationDTO();
            app.setApplicationName(param.getApplicationName());
            app.setRiskCount(param.getMachineRiskCount());
            app.setTotalCount(param.getMachineTotalCount());
            apps.add(app);
        }
        dto.setApplicationCount(paramList.size());
        dto.setMachineCount(riskMachineCount);
        dto.setApplicationList(apps);
        return dto;
    }

    private List<RiskMacheineDTO> convert2RiskMacheineDTO(List<ReportMachineResult> paramList) {
        List<RiskMacheineDTO> dataList = Lists.newArrayList();
        paramList.forEach(data -> {
            RiskMacheineDTO dto = new RiskMacheineDTO();
            dto.setId(data.getId());
            dto.setAppName(data.getApplicationName());
            dto.setMachineIp(data.getMachineIp());
            dto.setRiskContent(data.getRiskContent());
            dto.setAgentId(data.getAgentId());
            dataList.add(dto);
        });
        return dataList;
    }

    private MachineDetailDTO convert2MachineDetailDTO(ReportMachineResult param) {
        MachineDetailDTO dto = new MachineDetailDTO();
        dto.setId(param.getId());
        dto.setMachineIp(param.getMachineIp());
        dto.setApplicationName(param.getApplicationName());
        dto.setAgentId(param.getAgentId());
        dto.setRiskFlag(checkRisk(param.getRiskFlag()));
        parseBaseConfig(dto, param.getMachineBaseConfig());
        parseTpsConfig(dto, Arrays.asList(param.getMachineTpsTargetConfig()));
        return dto;
    }

    private List<ApplicationDTO> convert2ApplicationDTO(List<ReportApplicationSummaryResult> paramList) {
        List<ApplicationDTO> dataList = Lists.newArrayList();
        paramList.forEach(data -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setApplicationName(data.getApplicationName());
            dto.setRiskCount(data.getMachineRiskCount());
            dto.setTotalCount(data.getMachineTotalCount());
            dataList.add(dto);
        });
        return dataList;
    }

    private List<MachineDetailDTO> convert2MachineDetailDTO(List<ReportMachineResult> paramList) {
        List<MachineDetailDTO> dataList = Lists.newArrayList();
        paramList.forEach(data -> {
            MachineDetailDTO dto = new MachineDetailDTO();
            dto.setId(data.getId());
            dto.setMachineIp(data.getMachineIp());
            dto.setRiskFlag(checkRisk(data.getRiskFlag()));
            dto.setAgentId(data.getAgentId());
            dto.setApplicationName(data.getApplicationName());
            dto.setProcessName(dto.getMachineIp() + "|" + dto.getAgentId());
            dataList.add(dto);
        });
        return dataList;
    }

    private Boolean checkRisk(Integer riskFlag) {
        return (riskFlag != null && riskFlag == 1) ? true : false;
    }

    private void parseBaseConfig(MachineDetailDTO dto, String baseConfig) {
        try {
            if (StringUtils.isBlank(baseConfig)) {
                return;
            }
            JSONObject jsonObject = JSON.parseObject(baseConfig);
            dto.setCpuNum(jsonObject.getInteger(ReportConfigConstant.BASE_CPU_KEY));
            dto.setMemorySize(jsonObject.getBigDecimal(ReportConfigConstant.BASE_MEMORY_KEY));
            dto.setDiskSize(jsonObject.getBigDecimal(ReportConfigConstant.BASE_DISK_KEY));
            dto.setMbps(convertByte2Mb(jsonObject.getBigDecimal(ReportConfigConstant.BASE_MBPS_KEY)));
            dto.setGcCost(jsonObject.getBigDecimal(ReportConfigConstant.CHART_GC_COST));
            dto.setGcCount(jsonObject.getBigDecimal(ReportConfigConstant.CHART_GC_COUNT));
        } catch (Exception e) {
            log.error("Parse BaseConfig Error: config={}, error={}", baseConfig, e.getMessage());
        }
    }

    private void parseTpsConfig(MachineDetailDTO dto, List<String> configs) {
        try {
            if (CollectionUtils.isEmpty(configs)) {
                return;
            }
            //获取横坐标长度
            String[] time = getTpsConfigLength(configs);
            if (time == null || time.length == 0) {
                return;
            }
            int count = 0;
            Integer[] tps = new Integer[time.length];
            BigDecimal[] cpu = new BigDecimal[time.length];
            BigDecimal[] loading = new BigDecimal[time.length];
            BigDecimal[] memory = new BigDecimal[time.length];
            BigDecimal[] io = new BigDecimal[time.length];
            BigDecimal[] network = new BigDecimal[time.length];
            BigDecimal[] gcCost = new BigDecimal[time.length];
            BigDecimal[] gcCount = new BigDecimal[time.length];
            for (String config : configs) {
                if (StringUtils.isBlank(config)) {
                    continue;
                }
                TpsTargetArray array = JSON.parseObject(config, TpsTargetArray.class);
                if (array == null) {
                    continue;
                }
                //计算累计值
                for (int i = 0; i < time.length; i++) {
                    tps[i] = (tps[i] != null ? tps[i] : 0) + array.getTps()[i];
                    cpu[i] = (cpu[i] != null ? cpu[i] : ZERO).add(array.getCpu()[i]);
                    loading[i] = ((loading[i] != null) ? loading[i] : ZERO).add(array.getLoading()[i]);
                    memory[i] = (memory[i] != null ? memory[i] : ZERO).add(array.getMemory()[i]);
                    io[i] = (io[i] != null ? io[i] : ZERO).add(array.getIo()[i]);
                    network[i] = (network[i] != null ? network[i] : ZERO).add(array.getNetwork()[i]);
                    gcCost[i] = (gcCost[i] != null ? gcCost[i] : ZERO).add(array.getGcCost() != null ? array.getGcCost()[i] : ZERO);
                    gcCount[i] = (gcCount[i] != null ? gcCount[i] : ZERO).add(array.getGcCount() != null ? array.getGcCount()[i] : ZERO);
                }
                count++;
            }
            //tps计算累加值， 其他计算平均值
            for (int i = 0; i < time.length; i++) {
                cpu[i] = avg(cpu[i], count);
                loading[i] = avg(loading[i], count);
                memory[i] = avg(memory[i], count);
                io[i] = avg(io[i], count);
                network[i] = avg(network[i], count);
                gcCost[i] = avg(gcCost[i], count);
                gcCount[i] = avg(gcCount[i], count);
            }
            MachineDetailDTO.MachineTPSTargetDTO tpsTargetDTO = new MachineDetailDTO().new MachineTPSTargetDTO();
            tpsTargetDTO.setTime(time);
            tpsTargetDTO.setTps(tps);
            tpsTargetDTO.setCpu(cpu);
            tpsTargetDTO.setLoad(loading);
            tpsTargetDTO.setMemory(memory);
            tpsTargetDTO.setIo(io);
            tpsTargetDTO.setMbps(network);
            tpsTargetDTO.setGcCost(gcCost);
            tpsTargetDTO.setGcCount(gcCount);
            dto.setTpsTarget(tpsTargetDTO);
        } catch (Exception e) {
            log.error("Parse PtsConfig Error:, error={}", e.getMessage());
        }
    }

    private String[] getTpsConfigLength(List<String> configs) {
        String[] min = null;
        for (String config : configs) {
            if (StringUtils.isBlank(config)) {
                continue;
            }
            TpsTargetArray array = JSON.parseObject(config, TpsTargetArray.class);
            if (array == null) {
                continue;
            }

            if(min == null || min.length > array.getTime().length) {
                min = array.getTime();
            }
        }
        return min;
    }

    private BigDecimal avg(BigDecimal num1, Integer count) {
        return num1.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }

    /**
     * 网络带宽单位转换 Byte转Mb
     *
     * @return
     */
    private BigDecimal convertByte2Mb(BigDecimal num) {
        if (num == null) {
            return ZERO;
        }
        return num.divide(new BigDecimal(1024 * 1024), 0, RoundingMode.HALF_UP);
    }

    private BigDecimal formatBigDecimalPercent(BigDecimal num) {
        if (num == null) {
            return ZERO;
        }
        return num.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal formatBigDecimal(Double num) {
        if (num == null || num.isNaN()) {
            return ZERO;
        }
        return new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
    }
}
