package io.shulie.takin.web.biz.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.pradar.MiddlewareType;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.MD5Util;
import io.shulie.amdb.common.dto.link.topology.*;
import io.shulie.amdb.common.enums.NodeTypeEnum;
import io.shulie.amdb.common.enums.NodeTypeGroupEnum;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.*;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
import io.shulie.takin.web.common.enums.activity.info.RpcTypeEnum;
import io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum;
import io.shulie.takin.web.data.common.InfluxDatabaseManager;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.baseserver.TraceMetricsResult;
import io.shulie.takin.web.ext.entity.e2e.E2eBaseStorageParam;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;
import io.shulie.takin.web.ext.entity.e2e.E2eStorageRequest;
import io.shulie.takin.web.ext.util.E2ePluginUtils;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 链路拓扑图 接口--拓扑图信息从AMDB获取
 *
 * @author
 */
@Service
@Slf4j
public class LinkTopologyService extends CommonService {

    @Autowired
    private ApplicationMiddlewareService applicationMiddlewareService;


    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationEntranceClient applicationEntranceClient;

    @Autowired
    private InfluxDatabaseManager influxDBManager;

    @Autowired
    private ActivityService activityService;

    private static String pradarDatabase = "pradar";

    public static final double INIT = 0.0; // db 没有数据的初始值


    /**
     * 拓扑图查询, 处理
     *
     * @param request 请求入参
     *
     * @return 拓扑图相关
     */
    public ApplicationEntranceTopologyResponse getApplicationEntrancesTopology(
            ApplicationEntranceTopologyQueryRequest request) {

        // 大数据查询拓扑图
        LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
                request.getApplicationName(), request.getLinkId(), request.getServiceName(), request.getMethod(),
                request.getRpcType(), request.getExtend());

        // final result
        ApplicationEntranceTopologyResponse applicationEntranceTopologyResponse
                = new ApplicationEntranceTopologyResponse();

        // 查询 amdb 失败的情况
        if (applicationEntrancesTopology == null) {
            applicationEntranceTopologyResponse.setNodes(Lists.newArrayList());
            applicationEntranceTopologyResponse.setEdges(Lists.newArrayList());
            applicationEntranceTopologyResponse.setExceptions(Lists.newArrayList());
            return applicationEntranceTopologyResponse;
        }

        // 成功查询 amdb, 并处理
        processData(request, applicationEntrancesTopology, applicationEntranceTopologyResponse);

        return applicationEntranceTopologyResponse;
    }

    private void processData(
            ApplicationEntranceTopologyQueryRequest request,
            LinkTopologyDTO applicationEntrancesTopology,
            ApplicationEntranceTopologyResponse applicationEntranceTopologyResponse) {

        /* key:nodeId ,value: node */
        Map<String, LinkNodeDTO> nodeMap = Maps.newHashMap();
        /* key:nodeId ,value: 访问到node的边 */
        Map<String, List<LinkEdgeDTO>> providerEdgeMap = Maps.newHashMap();
        /* key:nodeId ,value: 从node开始的边 */
        Map<String, List<LinkEdgeDTO>> callEdgeMap = Maps.newHashMap();
        /* key:applicationName ,value: managerName */
        Map<String, String> managerMap = Maps.newHashMap();
        /* key:applicationName ,value: 应用实例节点 */
        Map<String, List<ApplicationNodeDTO>> appNodeMap = Maps.newHashMap();

        // nodes
        //SY:将节点的ID保存到map里面-节点信息
        List<LinkNodeDTO> nodes = applicationEntrancesTopology.getNodes();
        if (CollectionUtils.isNotEmpty(nodes)) {
            nodeMap = nodes.stream().collect(Collectors.toMap(LinkNodeDTO::getNodeId, self -> self));
            managerMap = this.getManagers(nodes);
            appNodeMap = this.getAppNodes(nodes);
        }

        // edges
        //SY:边信息
        List<LinkEdgeDTO> edges = applicationEntrancesTopology.getEdges();
        if (CollectionUtils.isNotEmpty(edges)) {
            providerEdgeMap = edges.stream().collect(Collectors.groupingBy(LinkEdgeDTO::getTargetId));//SY:根据边做分组
            callEdgeMap = edges.stream().collect(Collectors.groupingBy(LinkEdgeDTO::getSourceId));
        }

        // 节点转换
        //SY:根据不同的节点类型处理转化-加入provider service
        applicationEntranceTopologyResponse.setNodes(this.convertNodes(applicationEntrancesTopology, nodeMap,
            providerEdgeMap, callEdgeMap, managerMap, appNodeMap));
        // 边 转换
        applicationEntranceTopologyResponse.setEdges(this.convertEdges(applicationEntrancesTopology, nodeMap,
            providerEdgeMap, callEdgeMap, request, applicationEntranceTopologyResponse));

        // 异常转换
        applicationEntranceTopologyResponse.setExceptions(
            this.getExceptionsFromNodes(applicationEntranceTopologyResponse.getNodes()));
    }

    /**
     * @param activityId 业务活动 id
     * @param startDateTime 拓扑图的 开始时间
     * @param endDateTime   拓扑图的 结束时间
     * @param allTotalCountStartDateTime    拓扑图的 线上总调用量指标的 开始时间
     */
    public void fillMetrics(Long activityId,
                            ApplicationEntranceTopologyResponse topologyResponse,
                            LocalDateTime startDateTime,
                            LocalDateTime endDateTime,
                            LocalDateTime allTotalCountStartDateTime, FlowTypeEnum flowTypeEnum) {

        Boolean metricsType = null;
        // 压测流量(true)，业务流量(false)，混合流量(null)
        if (FlowTypeEnum.PRESSURE_MEASUREMENT.equals(flowTypeEnum)) {
            metricsType = true;
        } else if (FlowTypeEnum.BUSINESS.equals(flowTypeEnum)) {
            metricsType = false;
        }

        // startTime
        long startMilli = startDateTime.toInstant(ZoneOffset.of("+0")).toEpochMilli();
        long allTotalCountStartMilli = allTotalCountStartDateTime.toInstant(ZoneOffset.of("+0")).toEpochMilli();

        // endTime
        long endMilli = endDateTime.toInstant(ZoneOffset.of("+0")).toEpochMilli();

        // 由于 机器性能 / Trace数据量 等问题， 大数据在入库时 时间有延迟
        // 这里要查询真实 Trace数据的时间范围，应该是小于 startMilli
        long realSeconds = getTracePeriod(startMilli, endMilli, startDateTime, endDateTime);

        /*
        填充 Node
            总Tps / 总Rt
        */
        AbstractTopologyNodeResponse rootNode = null;

        // 查询 瓶颈阈值 配置
        List<E2eExceptionConfigInfoExt> bottleneckConfig = Lists.newArrayList();
        if (WebPluginUtils.checkUserData() && E2ePluginUtils.checkE2ePlugin()) {
            bottleneckConfig = E2ePluginUtils.getExceptionConfig(WebPluginUtils.getCustomerId());
        }
        // 查询 该业务活动 的所有开关状态
        List<ActivityNodeState> dbActivityNodeServiceState = activityService.getActivityNodeServiceState(activityId);

        List<AbstractTopologyNodeResponse> allNodes = topologyResponse.getNodes();
        for (AbstractTopologyNodeResponse node : allNodes) {
            // 填充 节点服务的 总调用量 / 总成功率 / 总Tps / 总Rt
            TopologyAppNodeResponse appnode = (TopologyAppNodeResponse) node;
            if (appnode.getProviderService() != null) {
                List<AppProviderInfo> appProviderInfos =
                        fillAppNodeServiceSuccessRateAndRt(
                                activityId, appnode, startDateTime, startMilli,
                                endMilli, realSeconds, metricsType, bottleneckConfig,
                                dbActivityNodeServiceState);

                // 设置 拓扑图中节点上显示哪一个服务性能指标
                setTopologyNodeServiceMetrics(node, appProviderInfos);
                // 设置业务活动层级的瓶颈
                setTopologyLevelBottleneck(topologyResponse);
            }

            if (node.getRoot()) {
                rootNode = node;
            }
        }

        /*
        填充 Edge
            总调用量 / 主干
        */
        List<ApplicationEntranceTopologyEdgeResponse> reduceEdges = topologyResponse.getEdges();
        for (ApplicationEntranceTopologyEdgeResponse edge : reduceEdges) {
            edge.setAllTotalCount(getAllServiceAllTotalCount(allNodes, edge));

            edge.setMain(false);
        }

        /*
        确定 主干
        */
        if (rootNode == null) {
            log.info("no entrance node");
            return;
        }
        int loopCounter = 0;
        setMainEdge(reduceEdges, rootNode.getId(), loopCounter);
    }

    private void setTopologyLevelBottleneck(ApplicationEntranceTopologyResponse topologyResponse) {
        for (AbstractTopologyNodeResponse node : topologyResponse.getNodes()) {
            if (node.isHasL1Bottleneck()) {
                topologyResponse.setHasL1Bottleneck(true);
                topologyResponse.setL1bottleneckNum(node.getL1bottleneckNum() + topologyResponse.getL1bottleneckNum());
            }
            if (node.isHasL2Bottleneck()) {
                topologyResponse.setHasL2Bottleneck(true);
                topologyResponse.setL2bottleneckNum(node.getL2bottleneckNum() + topologyResponse.getL2bottleneckNum());
            }
        }
    }

    private double getAllServiceAllTotalCount(
            List<AbstractTopologyNodeResponse> allNodes,
            ApplicationEntranceTopologyEdgeResponse edge) {

        double allServiceAllTotalCount = 0.0;

        // edge 总调用量
        for (AbstractTopologyNodeResponse node : allNodes) {
            if (edge.getTarget().equals(node.getId())) {
                // 获取 节点 所有服务
                List<AppProviderInfo> providerService = node.getProviderService();
                if (providerService == null) continue;

                for (AppProviderInfo appProviderInfo : providerService) {
                    for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                        for (AppProvider realProvider : appProvider.getContainRealAppProvider()) {
                            if ((realProvider.getSource().equals(edge.getSource()) &&
                                    realProvider.getTarget().equals(edge.getTarget()))) {
                                allServiceAllTotalCount = bigDecimalAdd(allServiceAllTotalCount, realProvider.getServiceAllTotalCount());
                            }
                        }
                    }
                }
            }
        }
        return allServiceAllTotalCount;
    }

    private void setTopologyNodeServiceMetrics(AbstractTopologyNodeResponse node, List<AppProviderInfo> appProviderInfos) {
        // init
        Double allTotalCount = INIT;
        Double allSuccessCount = INIT; // 总成功调用次数
        Double allTotalTps = INIT;
        Double allTotalRt = INIT;

        for (AppProviderInfo appProviderInfo : appProviderInfos) {
            for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                // 如果服务打开，则更新值
                if (appProvider.getSwitchState()) {
                    allTotalCount = bigDecimalAdd(allTotalCount, appProvider.getServiceAllTotalCount());
                    allSuccessCount = bigDecimalAdd(allSuccessCount, appProvider.getAllSuccessCount());
                    allTotalTps = bigDecimalAdd(allTotalTps, appProvider.getServiceAllTotalTps());
                    allTotalRt = bigDecimalAdd(allTotalRt, appProvider.getAllTotalRt());
                }
            }
        }

        // 在 node 上赋值
        node.setServiceAllTotalCount(allTotalCount);
        node.setServiceAllSuccessRate(bigDecimalDivide(allSuccessCount, allTotalCount));
        node.setServiceAllTotalTps(allTotalTps);
        node.setServiceRt(bigDecimalDivide(allTotalRt, allTotalCount));
    }

    private List<AppProviderInfo> fillAppNodeServiceSuccessRateAndRt(
            Long activityId, TopologyAppNodeResponse node, LocalDateTime startDateTime, long startMilli, long endMilli,
            long realSeconds, Boolean metricsType, List<E2eExceptionConfigInfoExt> bottleneckConfig,
            List<ActivityNodeState> dbActivityNodeServiceState) {

        // 节点 所有有服务，包含 不同的中间件，不同的serviceMethod
        List<AppProvider> allAppProviderServiceList = new ArrayList<>();

        List<AppProviderInfo> providerService = node.getProviderService();
        for (AppProviderInfo appProviderInfo : providerService) {

            for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                appProvider.setContainRealAppProvider(new ArrayList<>());

                for (LinkEdgeDTO linkEdgeDTO : appProvider.getContainEdgeList()) {
                    String eagleId = linkEdgeDTO.getEagleId();
                    // 根据服务边，查询指标
                    AppProvider appProviderFromDb =
                            queryMetricsFromDb(startMilli, endMilli, realSeconds, metricsType, eagleId);

                    appProviderFromDb.setBeforeApps(appProvider.getBeforeAppsMap().get(linkEdgeDTO.getSourceId()));
                    appProviderFromDb.setOwnerApps(appProvider.getOwnerApps());
                    appProviderFromDb.setEagleId(eagleId);
                    appProviderFromDb.setSource(linkEdgeDTO.getSourceId());
                    appProviderFromDb.setTarget(linkEdgeDTO.getTargetId());
                    appProviderFromDb.setRpcType(linkEdgeDTO.getRpcType());
                    appProviderFromDb.setServiceName(appProvider.getServiceName());
                    appProviderFromDb.setMiddlewareName(appProvider.getMiddlewareName());

                    // 瓶颈类型 init
                    int rateBottleneckType = -1; // 瓶颈类型(-1 没有瓶颈)
                    appProviderFromDb.setAllSuccessRateBottleneckType(rateBottleneckType);
                    appProviderFromDb.setAllTotalRtBottleneckType(rateBottleneckType);
                    appProviderFromDb.setAllSqlTotalRtBottleneckType(rateBottleneckType);

                    if (!appProviderFromDb.getServiceAllTotalCount().equals(INIT)) { // 如果不是初始值，再计算瓶颈
                        //SY:瓶颈计算+落库
                        computeBottleneck(startDateTime, activityId, bottleneckConfig, appProviderFromDb);
                    }

                    appProvider.getContainRealAppProvider().add(appProviderFromDb);
                }

                // 计算出每条真实边的指标后，再计算平均指标，用来对节点的某一服务赋值
                fillNodeServiceMetrics(appProvider);

                allAppProviderServiceList.add(appProvider);
                appProvider.setBeforeAppsMap(null);
            }
        }

        if (!allAppProviderServiceList.isEmpty()) {
            // 查询 并 设置 节点服务 开关状态
            setNodeServiceState(activityId, node, dbActivityNodeServiceState, allAppProviderServiceList);
            // 设置 节点层次的瓶颈数据
            setNodeBottleneck(node, allAppProviderServiceList);
        }

        return providerService;
    }

    private void setNodeBottleneck(TopologyAppNodeResponse node, List<AppProvider> allAppProviderServiceList) {
        // 一般瓶颈 [false 没有瓶颈 | true 有一般瓶颈]
        boolean hasL1Bottleneck = false;
        // 一般瓶颈数量
        int L1bottleneckNum = 0;

        // 严重瓶颈 [false 没有瓶颈 | true 有严重瓶颈]
        boolean hasL2Bottleneck = false;
        // 严重瓶颈数量
        int L2bottleneckNum = 0;

        for (AppProvider provider : allAppProviderServiceList) {
            for (AppProvider appProvider : provider.getContainRealAppProvider()) {
                if (RpcTypeEnum.APP.getValue().equals(appProvider.getRpcType())) {
                    if ((appProvider.getAllTotalRtBottleneckType() == 1)) {
                        hasL1Bottleneck = true;
                        L1bottleneckNum++;
                    } else if ((appProvider.getAllTotalRtBottleneckType() == 2)) {
                        hasL2Bottleneck = true;
                        L2bottleneckNum++;
                    }
                } else if (RpcTypeEnum.DB.getValue().equals(appProvider.getRpcType())) {
                    if ((appProvider.getAllSqlTotalRtBottleneckType() == 1)) {
                        hasL1Bottleneck = true;
                        L1bottleneckNum++;
                    } else if ((appProvider.getAllSqlTotalRtBottleneckType() == 2)) {
                        hasL2Bottleneck = true;
                        L2bottleneckNum++;
                    }
                }

                if (RpcTypeEnum.APP.getValue().equals(appProvider.getRpcType()) ||
                        RpcTypeEnum.DB.getValue().equals(appProvider.getRpcType())) {
                    if ((appProvider.getAllSuccessRateBottleneckType() == 1)) {
                        hasL1Bottleneck = true;
                        L1bottleneckNum++;
                    } else if ((appProvider.getAllSuccessRateBottleneckType() == 2)) {
                        hasL2Bottleneck = true;
                        L2bottleneckNum++;
                    }
                }
            }
        }

        node.setHasL1Bottleneck(hasL1Bottleneck);
        node.setL1bottleneckNum(L1bottleneckNum);
        node.setHasL2Bottleneck(hasL2Bottleneck);
        node.setL2bottleneckNum(L2bottleneckNum);
    }

    private void fillNodeServiceMetrics(AppProvider appProvider) {
        // init
        Double serviceAllTotalCount = INIT;
        Double allSuccessCount = INIT; // 总成功调用次数
        Double serviceAllTotalTps = INIT;
        Double allTotalRt = INIT;
        Double serviceAllMaxRt = INIT;

        // 将同一服务 的多条调用线上 的指标平均
        for (AppProvider realPro : appProvider.getContainRealAppProvider()) {
            serviceAllTotalCount = bigDecimalAdd(serviceAllTotalCount, realPro.getServiceAllTotalCount());
            allSuccessCount = bigDecimalAdd(allSuccessCount, realPro.getAllSuccessCount());
            serviceAllTotalTps = bigDecimalAdd(serviceAllTotalTps, realPro.getServiceAllTotalTps());

            allTotalRt = bigDecimalAdd(allTotalRt, realPro.getAllTotalRt());
            serviceAllMaxRt = Math.max(serviceAllMaxRt, realPro.getServiceAllMaxRt());
        }

        // 节点中 某个服务的 4 类性能指标
        appProvider.setServiceAllTotalCount(serviceAllTotalCount);
        // 成功率（入口1+入口2+入口3 成功次数）/（入口1+入口2+入口3 总次数）
        appProvider.setAllSuccessCount(allSuccessCount);
        appProvider.setServiceAllSuccessRate(bigDecimalDivide(appProvider.getAllSuccessCount(), serviceAllTotalCount));
        // tps 入口1+入口2+入口3 tps
        appProvider.setServiceAllTotalTps(serviceAllTotalTps);
        // rt （入口1+入口2+入口3 总耗时） /（入口1+入口2+入口3 总次数）
        appProvider.setAllTotalRt(allTotalRt);
        appProvider.setServiceAvgRt(bigDecimalDivide(appProvider.getAllTotalRt(), serviceAllTotalCount));
        appProvider.setServiceRt(appProvider.getServiceAvgRt());
        appProvider.setServiceAllMaxRt(serviceAllMaxRt);
    }

    private void computeBottleneck(
            LocalDateTime startDateTime, Long activityId,
            List<E2eExceptionConfigInfoExt> bottleneckConfig, AppProvider appProvider) {

        E2eStorageRequest storageRequest = new E2eStorageRequest();
        storageRequest.setRt(appProvider.getServiceAvgRt());
        storageRequest.setMaxRt(appProvider.getServiceAvgRt()); // SLOW_SQL 用
        storageRequest.setSuccessRate(appProvider.getServiceAllSuccessRate());

        //SY:调用瓶颈计算接口
        Map<Integer, Integer> resultMap = E2ePluginUtils.bottleneckCompute(storageRequest, bottleneckConfig);

        // 没有瓶颈 则返回
        if (resultMap.isEmpty()) {
            return;
        }

        // 有瓶颈，准备入库
        E2eBaseStorageParam baseStorageParam = new E2eBaseStorageParam();
        baseStorageParam.setSuccessRate(appProvider.getServiceAllSuccessRate());
        baseStorageParam.setRt(appProvider.getServiceAvgRt()); // 预设
        baseStorageParam.setEdgeId(appProvider.getEagleId());
        baseStorageParam.setStartTime(DateUtils.convertLocalDateTimeToUDate(startDateTime.plusHours(8)));
        baseStorageParam.setServiceName(appProvider.getOwnerApps() + "#" + appProvider.getServiceName());
        baseStorageParam.setRpcType(appProvider.getRpcType());
        baseStorageParam.setActivityId(activityId);

        // 卡慢 rt
        if (RpcTypeEnum.APP.getValue().equals(baseStorageParam.getRpcType())) {
            Integer rtInt = resultMap.get(1);
            if (rtInt != null) {
                baseStorageParam.setType(1);
                baseStorageParam.setLevel(rtInt);

                // 入 瓶颈异常库
                Long storage = E2ePluginUtils.bottleneckStorage(baseStorageParam);
                appProvider.setRtBottleneckId(storage);
                appProvider.setAllTotalRtBottleneckType(rtInt);
            }
        }

        // successRate
        Integer successRateInt = resultMap.get(2);
        if (successRateInt != null) {
            baseStorageParam.setType(2);
            baseStorageParam.setLevel(successRateInt);

            // 入 瓶颈异常库
            Long storage = E2ePluginUtils.bottleneckStorage(baseStorageParam);
            appProvider.setSuccessRateBottleneckId(storage);
            appProvider.setAllSuccessRateBottleneckType(successRateInt);
        }

        // 慢SQL 计算的指标是maxRt
        if (RpcTypeEnum.DB.getValue().equals(baseStorageParam.getRpcType())) {
            Integer DbRtInt = resultMap.get(4);
            if (DbRtInt != null) {
                baseStorageParam.setType(4);
                baseStorageParam.setLevel(DbRtInt);

                // 入 瓶颈异常库
                Long storage = E2ePluginUtils.bottleneckStorage(baseStorageParam);
                appProvider.setRtSqlBottleneckId(storage);
                appProvider.setAllSqlTotalRtBottleneckType(DbRtInt);
            }
        }

    }

    private void setNodeServiceState(Long activityId, TopologyAppNodeResponse node,
                                     List<ActivityNodeState> dbActivityNodeServiceState,
                                     List<AppProvider> appProviderList) {

        String nodeLabel = node.getLabel();

        // 找到当前节点的开关状态
        List<ActivityNodeState> currentNodeStateList = new ArrayList<>();
        for (ActivityNodeState activityNodeState : dbActivityNodeServiceState) {
            if (nodeLabel.equals(activityNodeState.getOwnerApp())) {
                currentNodeStateList.add(activityNodeState);
            }
        }

        // 循环节点的服务
        for (AppProvider appProvider : appProviderList) {
            String nodeServiceName = appProvider.getMiddlewareName()
                    + ":"
                    + appProvider.getServiceName();

            for (ActivityNodeState dbCurrentNodeState : currentNodeStateList) {
                // 如果该 node 在 db 中, 有开关记录, 则用 db 中的状态
                if (dbCurrentNodeState.getServiceName().equals(nodeServiceName)) {
                    appProvider.setSwitchState(dbCurrentNodeState.isState());
                }
            }

            // 开关记录循环结束，如果该服务在 db 中， 没有开关状态，则默认 打开服务
            if (appProvider.getSwitchState() == null) {
                activityService.setActivityNodeState(activityId, nodeLabel, nodeServiceName, true);
                appProvider.setSwitchState(true);
            }
        }
    }

    private AppProvider queryMetricsFromDb(long startMilli, long endMilli, long realSeconds, Boolean metricsType, String eagleId) {
        String allTotalTpsAndRtCountQuerySql =
                "SELECT" +
                        " SUM(successCount) as allSuccessCount," +
                        " SUM(totalCount) as allTotalCount," +
                        " SUM(totalTps) as allTotalTps," +
                        " MAX(maxRt) as allMaxRt," +
                        " SUM(totalRt) as allTotalRt" +
                        " FROM trace_metrics" +
                        " where" +
                        " edgeId = '" + eagleId + "'" +
                        " and time >= " + formatTimestamp(startMilli) +
                        " and time <= " + formatTimestamp(endMilli);

        // 如果不是 混合流量 则需要增加条件
        if (null != metricsType) {
            allTotalTpsAndRtCountQuerySql += " AND clusterTest = '" + metricsType + "'";
        }
        Collection<TraceMetricsResult> allTotalTpsAndRtCountResult = influxDBManager.query(TraceMetricsResult.class, allTotalTpsAndRtCountQuerySql, pradarDatabase);
        ArrayList<TraceMetricsResult> allTotalTpsAndRtCountResults = new ArrayList<>(allTotalTpsAndRtCountResult);

        AppProvider appProvider = new AppProvider();

        // 指标 初始化
        appProvider.setServiceAllTotalCount(INIT); // 总调用量
        appProvider.setServiceAllSuccessRate(INIT); // 总成功率
            appProvider.setAllSuccessCount(INIT); // 总成功调用次数
        appProvider.setServiceAllTotalTps(INIT); // 总Tps

        appProvider.setServiceRt(INIT); // 总Rt
        appProvider.setServiceAvgRt(INIT); // 平均Rt
            appProvider.setAllTotalRt(INIT); // 总调用Rt
        appProvider.setServiceAllMaxRt(INIT); // maxRt

        if (allTotalTpsAndRtCountResults.size() != 0) {
            TraceMetricsResult traceMetricsResult = allTotalTpsAndRtCountResults.get(0);

            // 1)
            // 总调用量
            Double allTotalCount = traceMetricsResult.getAllTotalCount();
            appProvider.setServiceAllTotalCount(allTotalCount);

            // 2)
            // 总成功调用次数
            appProvider.setAllSuccessCount(traceMetricsResult.getAllSuccessCount());
            // 总成功率     总成功调用次数 / 总调用次数    SUM(successCount) / SUM(totalCount)
            double allSuccessCountRate = bigDecimalDivide(appProvider.getAllSuccessCount(), allTotalCount);
            appProvider.setServiceAllSuccessRate(allSuccessCountRate);

            // 3)
            // 平均Tps    总调用次数 / 总秒数    SUM(totalCount) / 总秒数
            double allTotalTps = bigDecimalDivide(allTotalCount, new Long(realSeconds).doubleValue());
            appProvider.setServiceAllTotalTps(allTotalTps);

            // 4)
            // 总调用Rt
            appProvider.setAllTotalRt(traceMetricsResult.getAllTotalRt());
            // 平均 Rt    总调用Rt / 总调用次数    SUM(totalRt) / SUM(totalCount)
            double avgRt = bigDecimalDivide(appProvider.getAllTotalRt(), allTotalCount);
            appProvider.setServiceAvgRt(avgRt);
            appProvider.setServiceRt(appProvider.getServiceAvgRt());
            // 最大 Rt    MAX(maxRt)
            double allMaxRt = traceMetricsResult.getAllMaxRt();
            appProvider.setServiceAllMaxRt(allMaxRt);
        }
        return appProvider;
    }

    private long getTracePeriod(long startMilli, long endMilli, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        String firstTime =
                "SELECT" +
                        " time, totalTps" +
                        " FROM trace_metrics" +
                        " where" +
                        " time >= " + formatTimestamp(startMilli) +
                        " and time <= " + formatTimestamp(endMilli) +
                        " order by time" +
                        " limit 1";

        String lastTime =
                "SELECT" +
                        " time, totalTps" +
                        " FROM trace_metrics" +
                        " where" +
                        " time >= " + formatTimestamp(startMilli) +
                        " and time <= " + formatTimestamp(endMilli) +
                        " order by time desc" +
                        " limit 1";

        Collection<TraceMetricsResult> firstTimeResult = influxDBManager.query(TraceMetricsResult.class, firstTime, pradarDatabase);
        Collection<TraceMetricsResult> lastTimeResult = influxDBManager.query(TraceMetricsResult.class, lastTime, pradarDatabase);
        ArrayList<TraceMetricsResult> firstTraceMetricsResultList = new ArrayList<>(firstTimeResult);
        ArrayList<TraceMetricsResult> lastTraceMetricsResultList = new ArrayList<>(lastTimeResult);

        long seconds = 0;
        if (firstTraceMetricsResultList.size() != 0) {
            TraceMetricsResult firstTraceMetricsResult = firstTraceMetricsResultList.get(0);
            TraceMetricsResult lastTraceMetricsResult = lastTraceMetricsResultList.get(0);
            Duration d3 = Duration.between(firstTraceMetricsResult.getTime(), lastTraceMetricsResult.getTime());
            seconds = d3.getSeconds();

            log.info("get data between [{}] --> [{}], [{}] seconds ",
                    getLocalDateTime(firstTraceMetricsResult),
                    getLocalDateTime(lastTraceMetricsResult),
                    seconds);
        }

        long realSeconds = seconds;
        double realMinute = bigDecimalDivide(new Long(realSeconds).doubleValue(), new Long(60L).doubleValue());
        log.info("get real trace-data in [{}] minute, query data in [{} - {}]",
                realMinute, startDateTime, endDateTime);

        return realSeconds;
    }

    private LocalDateTime getLocalDateTime(TraceMetricsResult firstTraceMetricsResult) {
        return LocalDateTime.ofInstant(firstTraceMetricsResult.getTime(), ZoneId.systemDefault());
    }

    private void setMainEdge(
            List<ApplicationEntranceTopologyEdgeResponse> allEdges,
            String node,
            int loopCounter) {

        // 防止 (from / to) 是一个 node, 循环调用自己
        if (loopCounter++ > allEdges.size()) {
            return;
        }

        // 找出 node 下游的连线 的集合 edgeListOfNode
        ArrayList<ApplicationEntranceTopologyEdgeResponse> edgeListOfNode = new ArrayList<>();
        for (ApplicationEntranceTopologyEdgeResponse edge : allEdges) {
            if (node.equals(edge.getSource())) {
                if (edge.getLabel().equalsIgnoreCase("tomcat")
                        || edge.getLabel().equalsIgnoreCase("dubbo")
                        || edge.getLabel().equalsIgnoreCase("http")) {
                    edgeListOfNode.add(edge);
                }
            }
        }

        // 连线集合 为空，说明没有下游了
        if (edgeListOfNode.size() == 0) return;

        // 找出 连线集合中 总调用量 最大的一条
        // 如果下游 调用量 均为 0， 则其中一条为主干
        int maxTotalCountIndex = 0;
        Double maxAllTotalCount = 0D;
        for (int i = 0; i < edgeListOfNode.size(); i++) {
            Double currentAllTotalCount = edgeListOfNode.get(i).getAllTotalCount();
            if (currentAllTotalCount > maxAllTotalCount) {
                maxAllTotalCount = currentAllTotalCount;
                maxTotalCountIndex = i;
            }
        }

        // 总调用量 最大的一条 设为 主干
        ApplicationEntranceTopologyEdgeResponse mainEdge = edgeListOfNode.get(maxTotalCountIndex);
        mainEdge.setMain(true);

        setMainEdge(allEdges, mainEdge.getTarget(), loopCounter);
    }

    public static double bigDecimalAdd(double a, double b) {
        BigDecimal a1 = new BigDecimal(a).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal b1 = new BigDecimal(b).setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal add = a1.add(b1);
        return add.doubleValue();
    }

    private double bigDecimalDivide(Double aDouble, Double bDouble) {
        BigDecimal a = new BigDecimal(aDouble).setScale(4, BigDecimal.ROUND_DOWN);
        BigDecimal b = new BigDecimal(bDouble).setScale(4, BigDecimal.ROUND_DOWN);

        BigDecimal zero = new BigDecimal("0.0").setScale(4, BigDecimal.ROUND_DOWN);
        if (b.equals(zero)) {
            return zero.doubleValue();
        }
        BigDecimal divide = a.divide(b, 2, BigDecimal.ROUND_HALF_UP);
        return divide.doubleValue();
    }

    private static long formatTimestamp(long timestamp) {
        String temp = timestamp + "000000";
        return Long.parseLong(temp);
    }

    private Map<String, List<ApplicationNodeDTO>> getAppNodes(List<LinkNodeDTO> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Maps.newHashMap();
        }
        List<String> appNames = nodes.stream()
                //.filter(node -> node.getNodeTypeGroup().equalsIgnoreCase(NodeTypeGroupEnum.APP.name()))
                .map(LinkNodeDTO::getNodeName)
                .collect(Collectors.toList());
        ApplicationNodeQueryDTO applicationNodeQueryDTO = new ApplicationNodeQueryDTO();
        applicationNodeQueryDTO.setPageSize(9999);
        applicationNodeQueryDTO.setAppNames(StringUtils.join(appNames, ","));
        PagingList<ApplicationNodeDTO> applicationNodeDtoPagingList = applicationClient.pageApplicationNodes(
                applicationNodeQueryDTO);
        if (applicationNodeDtoPagingList.getTotal() == 0) {
            return Maps.newHashMap();
        }
        return applicationNodeDtoPagingList.getList().stream().collect(
                Collectors.groupingBy(ApplicationNodeDTO::getAppName));
    }

    private Map<String, String> getManagers(List<LinkNodeDTO> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Maps.newHashMap();
        }
        List<String> applicationNames = nodes.stream().filter(node -> nodeIsApp(node) && !isVirtualNode(node)).map(
                LinkNodeDTO::getNodeName).collect(Collectors.toList());

        List<ApplicationResult> applicationsByName = applicationDAO.getApplicationByName(applicationNames);
        if (CollectionUtils.isEmpty(applicationsByName)) {
            return Maps.newHashMap();
        }
        Map<String, String> appNameManagerNameMap = applicationsByName.stream()
                .filter(app -> StringUtils.isNotBlank(app.getAppManagerName()))
                .collect(Collectors.toMap(ApplicationResult::getAppName, ApplicationResult::getAppManagerName));
        Map<String, String> map = Maps.newHashMap();
        for (LinkNodeDTO node : nodes) {
            if (nodeIsApp(node) && !isVirtualNode(node)) {
                map.put(node.getNodeName(), appNameManagerNameMap.get(node.getNodeName()));
            }
        }
        return map;
    }

    private List<AbstractTopologyNodeResponse> convertNodes(
            LinkTopologyDTO applicationEntrancesTopology,
            Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
            Map<String, List<LinkEdgeDTO>> callEdgeMap, Map<String, String> managerMap,
            Map<String, List<ApplicationNodeDTO>> appNodeMap) {

        if (CollectionUtils.isEmpty(applicationEntrancesTopology.getNodes())) {
            return Lists.newArrayList();
        }

        AtomicLong nextNumber = new AtomicLong();

        return applicationEntrancesTopology.getNodes().stream().map(node -> {
            // other
            if (NodeTypeGroupEnum.OTHER.getType().equals(node.getNodeTypeGroup())) {

                if (isVirtualNode(node)) {
                    TopologyVirtualNodeResponse nodeResponse = new TopologyVirtualNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setVirtualResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                    return nodeResponse;
                } else if (isOuterService(node)) {
                    TopologyOtherNodeResponse nodeResponse = new TopologyOtherNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setOtherResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                    return nodeResponse;
                } else if (isUnknownNode(node)) {
                    TopologyUnknownNodeResponse nodeResponse = new TopologyUnknownNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setUnknownResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                    return nodeResponse;
                } else {
                    TopologyOtherNodeResponse nodeResponse = new TopologyOtherNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setOtherResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                    return nodeResponse;
                }

            // 能区分类型的
            } else if (NodeTypeGroupEnum.APP.getType().equals(node.getNodeTypeGroup())) {
                TopologyAppNodeResponse nodeResponse = new TopologyAppNodeResponse();
                setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                setAppNodeResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                return nodeResponse;
            } else if (NodeTypeGroupEnum.OSS.getType().equals(node.getNodeTypeGroup())) {
                TopologyOssNodeResponse nodeResponse = new TopologyOssNodeResponse();
                setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                setOssNodeResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap);
                return nodeResponse;
            } else if (NodeTypeGroupEnum.CACHE.getType().equals(node.getNodeTypeGroup())) {
                TopologyCacheNodeResponse nodeResponse = new TopologyCacheNodeResponse();
                setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                setCacheNodeResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap);
                return nodeResponse;
            } else if (NodeTypeGroupEnum.DB.getType().equals(node.getNodeTypeGroup())) {
                TopologyDbNodeResponse nodeResponse = new TopologyDbNodeResponse();
                setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                setDbNodeResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap);
                return nodeResponse;
            } else if (NodeTypeGroupEnum.MQ.getType().equals(node.getNodeTypeGroup())) {
                TopologyMqNodeResponse nodeResponse = new TopologyMqNodeResponse();
                setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                setMqNodeResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap);
                return nodeResponse;
            } else if (NodeTypeGroupEnum.SEARCH.getType().equals(node.getNodeTypeGroup())) {
                TopologySearchNodeResponse nodeResponse = new TopologySearchNodeResponse();
                setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                setSearchNodeResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap);
                return nodeResponse;
            }
            return null;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setUnknownResponse(TopologyUnknownNodeResponse nodeResponse, LinkNodeDTO node,
                                    Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                    Map<String, List<LinkEdgeDTO>> callEdgeMap, Map<String, String> managerMap,
                                    Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    /**
     * 获取上游节点名称列表
     *
     * @return
     */
    private List<String> getUpAppNames(LinkNodeDTO node, Map<String, LinkNodeDTO> nodeMap,
                                       Map<String, List<LinkEdgeDTO>> providerEdgeMap) {
        if (MapUtils.isEmpty(providerEdgeMap) || CollectionUtils.isEmpty(providerEdgeMap.get(node.getNodeId()))) {
            return new ArrayList<>();
        }
        List<String> updateNodeIdList = providerEdgeMap.get(node.getNodeId()).stream().map(LinkEdgeDTO::getSourceId)
                .collect(
                        Collectors.toList());
        return updateNodeIdList.stream().map(upNodeId -> {
            LinkNodeDTO linkNodeDTO = nodeMap.get(upNodeId);
            if (linkNodeDTO != null) {
                return nodeMap.get(upNodeId).getNodeName();
            }
            return null;
        }).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
    }

    private void setOtherResponse(TopologyOtherNodeResponse nodeResponse, LinkNodeDTO node,
                                  Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                  Map<String, List<LinkEdgeDTO>> callEdgeMap, Map<String, String> managerMap,
                                  Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setVirtualResponse(TopologyVirtualNodeResponse nodeResponse, LinkNodeDTO node,
        Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
        Map<String, List<LinkEdgeDTO>> callEdgeMap, Map<String, String> managerMap,
        Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setMqNodeResponse(TopologyMqNodeResponse nodeResponse,
                                   LinkNodeDTO node,
                                   Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                   Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                   Map<String, List<ApplicationNodeDTO>> appNodeMap) {

        List<LinkEdgeDTO> linkEdgeDtoList = providerEdgeMap.get(node.getNodeId());
        List<MqInfo> mqs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(linkEdgeDtoList)) {
            mqs = linkEdgeDtoList.stream().map(edge -> {
                MqInfo mqInfo = new MqInfo();
                mqInfo.setTopic(edge.getService());
                return mqInfo;
            }).collect(Collectors.toList());
        }
        nodeResponse.setMq(mqs);
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        //SY:所有的MQ类型的服务都需要对外服务信息，不做任何过滤
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setDbNodeResponse(TopologyDbNodeResponse nodeResponse,
                                   LinkNodeDTO node,
                                   Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                   Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                   Map<String, List<ApplicationNodeDTO>> appNodeMap) {

        List<LinkEdgeDTO> linkEdgeDtoListS = providerEdgeMap.get(node.getNodeId());
        List<DbInfo> dbs = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(linkEdgeDtoListS)) {
            dbs = linkEdgeDtoListS.stream().map(edge -> {
                DbInfo db = new DbInfo();
                db.setTableName(edge.getMethod());
                return db;
            }).collect(Collectors.toList());
        }
        nodeResponse.setDb(dbs);
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setCacheNodeResponse(TopologyCacheNodeResponse nodeResponse,
                                      LinkNodeDTO node,
                                      Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                      Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                      Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setSearchNodeResponse(TopologySearchNodeResponse nodeResponse,
                                       LinkNodeDTO node,
                                       Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                       Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                       Map<String, List<ApplicationNodeDTO>> appNodeMap) {

        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setOssNodeResponse(TopologyOssNodeResponse nodeResponse,
                                    LinkNodeDTO node,
                                    Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                    Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                    Map<String, List<ApplicationNodeDTO>> appNodeMap) {

        List<LinkEdgeDTO> linkEdgeDtoList = providerEdgeMap.get(node.getNodeId());
        List<OssInfo> ossInfoLists = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(linkEdgeDtoList)) {
            ossInfoLists = linkEdgeDtoList.stream().map(edge -> {
                OssInfo ossInfo = new OssInfo();
                ossInfo.setFilePath(edge.getService());
                return ossInfo;
            }).collect(Collectors.toList());
        }
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setOss(ossInfoLists);
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private void setAppNodeResponse(TopologyAppNodeResponse nodeResponse,
                                    LinkNodeDTO node,
                                    Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                    Map<String, List<LinkEdgeDTO>> callEdgeMap, Map<String, String> managerMap,
                                    Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        nodeResponse.setManager(managerMap.get(node.getNodeName()));
        nodeResponse.setNodes(getNodeDetails(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setCallService(
                convertAppNodeCallService(node, nodeMap, providerEdgeMap, callEdgeMap, appNodeMap));
        nodeResponse.setProviderService(convertAppNodeProviderService(node, nodeMap, providerEdgeMap));
    }

    private List<AppProviderInfo> convertAppNodeProviderService(
            LinkNodeDTO node,
            Map<String, LinkNodeDTO> nodeMap,
            Map<String, List<LinkEdgeDTO>> providerEdgeMap) {

        // 本节点提供的边
        List<LinkEdgeDTO> providerEdge = providerEdgeMap.get(node.getNodeId());
        if (CollectionUtils.isEmpty(providerEdge)) {
            return Lists.newArrayList();
        }

        // 根据 中间件 分组
        Map<String, List<LinkEdgeDTO>> providerEdgeByMiddlewareName = providerEdge.stream().collect(
                Collectors.groupingBy(LinkEdgeDTO::getMiddlewareName));

        List<AppProviderInfo> collect = providerEdgeByMiddlewareName.entrySet().stream().map(edgeByMiddlewareName -> {
            AppProviderInfo appProviderInfo = new AppProviderInfo();
            appProviderInfo.setLabel(edgeByMiddlewareName.getKey()); // MiddlewareName

            List<AppProvider> datasource = getDatasource(node, nodeMap, edgeByMiddlewareName,
                    Lists.newArrayList());
            appProviderInfo.setDataSource(datasource);
            return appProviderInfo;
        }).collect(Collectors.toList());

        return collect;
    }

    private List<AppProvider> getDatasource(LinkNodeDTO node,
                                            Map<String, LinkNodeDTO> nodeMap,
                                            Map.Entry<String, List<LinkEdgeDTO>> edgeByMiddlewareName,
                                            List<AppProvider> datasource) {

        if (CollectionUtils.isEmpty(edgeByMiddlewareName.getValue())) {
            return datasource;
        }

        // 根据 ServiceMethod 再次分组
        Map<String, List<LinkEdgeDTO>> serviceMethodGroup = edgeByMiddlewareName.getValue().stream().collect(
                Collectors.groupingBy(this::getServiceMethod));

        datasource = serviceMethodGroup.entrySet().stream().map(item -> {
            AppProvider appProvider = new AppProvider();
            appProvider.setOwnerApps(node.getNodeName());
            appProvider.setMiddlewareName(edgeByMiddlewareName.getKey());
            appProvider.setServiceName(item.getKey());
            appProvider.setContainEdgeList(new ArrayList<>(item.getValue()));

            if (CollectionUtils.isNotEmpty(item.getValue())) {
                HashMap<String, String> sourceIdToNodeName = new HashMap<>();
                appProvider.setBeforeAppsMap(sourceIdToNodeName);

                // 设置上游应用(逗号分割的列表)
                List<String> keyList = item.getValue().stream()
                        .filter(Objects::nonNull)
                        .map(LinkEdgeDTO::getSourceId)
                        .collect(Collectors.toList());

                for (String id : keyList) {
                    LinkNodeDTO linkNodeDTO = nodeMap.get(id);
                    String nodeName = linkNodeDTO.getNodeName();
                    sourceIdToNodeName.put(id, nodeName);
                }

                String collect = item.getValue().stream()
                        .filter(Objects::nonNull)
                        .map(LinkEdgeDTO::getSourceId)
                        .filter(StrUtil::isNotBlank)
                        .map(nodeMap::get)
                        .filter(Objects::nonNull)
                        .map(LinkNodeDTO::getNodeName)
                        .filter(StrUtil::isNotBlank)
                        .collect(Collectors.joining(","));

                appProvider.setBeforeApps(collect);
            }

            return appProvider;
        }).collect(Collectors.toList());

        return datasource;
    }

    private String getServiceMethod(LinkEdgeDTO e) {
        return e.getService() + "#" + e.getMethod();
    }

    private List<AppCallInfo> convertAppNodeCallService(LinkNodeDTO node,
                                                        Map<String, LinkNodeDTO> nodeMap,
                                                        Map<String, List<LinkEdgeDTO>> providerEdgeMap, Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                                        Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        // 本服务调用的边
        List<LinkEdgeDTO> callEdges = callEdgeMap.get(node.getNodeId());
        if (CollectionUtils.isEmpty(callEdges)) {
            return Lists.newArrayList();
        }
        Map<String, List<LinkEdgeDTO>> excludeMqNodeWithEdge = callEdges.stream().collect(
                Collectors.groupingBy(LinkEdgeDTO::getTargetId));
        return getCallInfo(nodeMap, excludeMqNodeWithEdge, appNodeMap);
    }

    private List<AppCallInfo> getCallInfo(Map<String, LinkNodeDTO> nodeMap,
                                          Map<String, List<LinkEdgeDTO>> excludeMqNodeWithEdge,
                                          Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        return excludeMqNodeWithEdge.entrySet().stream().map(entry -> {
            LinkNodeDTO linkNodeDTO = nodeMap.get(entry.getKey());
            AppCallInfo appCallInfo
                    = new AppCallInfo();
            appCallInfo.setNodeType(NodeTypeResponseEnum
                    .getTypeByAmdbType(linkNodeDTO.getNodeTypeGroup()));
            appCallInfo.setLabel(linkNodeDTO.getNodeType().toUpperCase());
            appCallInfo.setDataSource(convertCallTypeInfo(entry.getValue(), linkNodeDTO, nodeMap, appNodeMap));
            return appCallInfo;
        }).collect(Collectors.toList());
    }

    private List<AppCallDatasourceInfo> convertCallTypeInfo(List<LinkEdgeDTO> edges,
                                                            LinkNodeDTO node,
                                                            Map<String, LinkNodeDTO> nodeMap,
                                                            Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        if (CollectionUtils.isEmpty(edges)) {
            return Lists.newArrayList();
        }

        if (NodeTypeGroupEnum.APP.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info1
                    = new AppCallDatasourceInfo();
            info1.setLabel("节点名称");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);

            AppCallDatasourceInfo info2
                    = new AppCallDatasourceInfo();
            info2.setLabel("节点");
            List<ApplicationNodeDTO> applicationNodeDtoList = appNodeMap.get(node.getNodeName());
            if (CollectionUtils.isNotEmpty(applicationNodeDtoList)) {
                info2.setDataSource(applicationNodeDtoList.stream().map(ApplicationNodeDTO::getIpAddress).collect(
                        Collectors.toList()));
            } else {
                info2.setDataSource(Lists.newArrayList());
            }
            infos.add(info2);

            AppCallDatasourceInfo info3
                    = new AppCallDatasourceInfo();
            info3.setLabel("服务");
            info3.setDataSource(
                    edges.stream().map(this::getServiceMethod).collect(Collectors.toList()));
            infos.add(info3);

            return infos;
        }

        if (NodeTypeGroupEnum.CACHE.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info1
                    = new AppCallDatasourceInfo();
            info1.setLabel("节点");
            NodeExtendInfoForCacheDTO extend = convertNodeExtendInfo(node.getExtendInfo(),
                    NodeExtendInfoForCacheDTO.class);
            info1.setDataSource(Lists.newArrayList(extend.getIp() + ":" + extend.getPort()));
            infos.add(info1);

            return infos;
        }
        if (NodeTypeGroupEnum.OSS.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info2
                    = new AppCallDatasourceInfo();
            info2.setLabel("文件路径");
            info2.setDataSource(edges.stream().map(LinkEdgeDTO::getService).collect(Collectors.toList()));
            infos.add(info2);

            return infos;
        }
        if (NodeTypeGroupEnum.MQ.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info1
                    = new AppCallDatasourceInfo();
            info1.setLabel("节点名称");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);

            AppCallDatasourceInfo info2
                    = new AppCallDatasourceInfo();
            info2.setLabel("Topic");
            info2.setDataSource(edges.stream().map(LinkEdgeDTO::getService).collect(Collectors.toList()));
            infos.add(info2);

            return infos;
        }
        if (NodeTypeGroupEnum.DB.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info1
                    = new AppCallDatasourceInfo();
            info1.setLabel("节点名称");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);

            AppCallDatasourceInfo info2
                    = new AppCallDatasourceInfo();
            info2.setLabel("库名称");
            info2.setDataSource(Lists.newArrayList(edges.stream().map(LinkEdgeDTO::getService).distinct()
                    .collect(Collectors.joining(","))));
            infos.add(info2);

            AppCallDatasourceInfo info3
                    = new AppCallDatasourceInfo();
            info3.setLabel("表名称");
            info3.setDataSource(Lists.newArrayList(edges.stream().map(LinkEdgeDTO::getMethod).distinct()
                    .collect(Collectors.joining(","))));
            infos.add(info3);

            return infos;
        }
        if (NodeTypeGroupEnum.SEARCH.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();
            AppCallDatasourceInfo info1
                    = new AppCallDatasourceInfo();
            info1.setLabel("节点名称");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);
            return infos;
        }
        if (NodeTypeGroupEnum.OTHER.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();
            AppCallDatasourceInfo info1
                    = new AppCallDatasourceInfo();
            info1.setLabel("节点名称");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);
            return infos;
        }
        throw new RuntimeException("不支持的节点类型:" + node.getNodeTypeGroup());
    }

    private void setNodeDefaultResponse(AbstractTopologyNodeResponse nodeResponse,
                                        LinkNodeDTO node,
                                        Map<String, LinkNodeDTO> nodeMap,
                                        Map<String, List<LinkEdgeDTO>> providerEdgeMap,
                                        Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                        AtomicLong nextNumber) {
        // 不是未知且不是三方服务
        if (!isUnknownNode(node) && !isOuterService(node)) {
            nodeResponse.setNodeType(
                    NodeTypeResponseEnum.getTypeByAmdbType(node.getNodeTypeGroup()));
        }
        // 如果是未知服务
        if (isUnknownNode(node)) {
            nodeResponse.setNodeType(NodeTypeResponseEnum.UNKNOWN);
        }
        // 第三方服务
        if (isOuterService(node)) {
            nodeResponse.setNodeType(NodeTypeResponseEnum.OUTER);
        }
        // 虚拟节点
        if (isVirtualNode(node)) {
            nodeResponse.setNodeType(NodeTypeResponseEnum.VIRTUAL);
        }
        nodeResponse.setLabel(getNodeInfoLabel(node, nextNumber));
        nodeResponse.setRoot(isRoot(node));
        nodeResponse.setId(node.getNodeId());
        nodeResponse.setUpAppNames(getUpAppNames(node, nodeMap, providerEdgeMap));
    }

    /**
     * 代码暂时看着是一模一样，未来不同类型应该是不一样的，这样写为了拓展。
     */
    private List<NodeDetailDatasourceInfo> getNodeDetails(LinkNodeDTO node,
                                                          Map<String, LinkNodeDTO> nodeMap,
                                                          Map<String, List<LinkEdgeDTO>> providerEdgeMap, Map<String, List<LinkEdgeDTO>> callEdgeMap,
                                                          Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        // 未知应用或者外部应用
        if (this.isUnknownNode(node) || this.isOuterService(node)) {
            NodeExtendInfoBaseDTO extendInfo = this.convertNodeExtendInfo(node.getExtendInfo(),
                NodeExtendInfoBaseDTO.class);
            NodeDetailDatasourceInfo nodeDetailDatasourceInfo = new NodeDetailDatasourceInfo();
            if (extendInfo == null) {
                nodeDetailDatasourceInfo.setNode("");
            } else {
                nodeDetailDatasourceInfo.setNode(String.format("%s:%s", extendInfo.getIp(), extendInfo.getPort()));
            }

            return Lists.newArrayList(nodeDetailDatasourceInfo);
        }

        if (NodeTypeGroupEnum.MQ.name().equals(node.getNodeTypeGroup())) {
            NodeExtendInfoForMQDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
                    NodeExtendInfoForMQDTO.class);
            NodeDetailDatasourceInfo nodeDetailDatasourceInfo
                    = new NodeDetailDatasourceInfo();
            nodeDetailDatasourceInfo.setNode(extendInfo.getIp() + ":" + extendInfo.getPort());
            return Lists.newArrayList(nodeDetailDatasourceInfo);
        }
        if (NodeTypeGroupEnum.CACHE.name().equals(node.getNodeTypeGroup())) {
            NodeExtendInfoForCacheDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
                    NodeExtendInfoForCacheDTO.class);
            NodeDetailDatasourceInfo nodeDetailDatasourceInfo
                    = new NodeDetailDatasourceInfo();
            nodeDetailDatasourceInfo.setNode(extendInfo.getIp() + ":" + extendInfo.getPort());
            return Lists.newArrayList(nodeDetailDatasourceInfo);
        }
        if (NodeTypeGroupEnum.DB.name().equals(node.getNodeTypeGroup())) {
            NodeExtendInfoForDBDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
                    NodeExtendInfoForDBDTO.class);
            NodeDetailDatasourceInfo nodeDetailDatasourceInfo
                    = new NodeDetailDatasourceInfo();
            nodeDetailDatasourceInfo.setNode(extendInfo.getIp() + ":" + extendInfo.getPort());
            return Lists.newArrayList(nodeDetailDatasourceInfo);
        }
        if (NodeTypeGroupEnum.OSS.name().equals(node.getNodeTypeGroup())) {
            NodeExtendInfoForOSSDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
                    NodeExtendInfoForOSSDTO.class);
            NodeDetailDatasourceInfo nodeDetailDatasourceInfo
                    = new NodeDetailDatasourceInfo();
            nodeDetailDatasourceInfo.setNode(extendInfo.getIp() + ":" + extendInfo.getPort());
            return Lists.newArrayList(nodeDetailDatasourceInfo);
        }
        if (NodeTypeGroupEnum.SEARCH.name().equals(node.getNodeTypeGroup())) {
            NodeExtendInfoForSearchDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
                    NodeExtendInfoForSearchDTO.class);
            NodeDetailDatasourceInfo nodeDetailDatasourceInfo
                    = new NodeDetailDatasourceInfo();
            nodeDetailDatasourceInfo.setNode(extendInfo.getIp() + ":" + extendInfo.getPort());
            return Lists.newArrayList(nodeDetailDatasourceInfo);
        }
        if (NodeTypeGroupEnum.APP.name().equals(node.getNodeTypeGroup())) {
            if (CollectionUtils.isNotEmpty(appNodeMap.get(node.getNodeName()))) {
                return appNodeMap.get(node.getNodeName()).stream().map(nodeDTO -> {
                    NodeDetailDatasourceInfo nodeDetailDatasourceInfo = new NodeDetailDatasourceInfo();
                    nodeDetailDatasourceInfo.setNode(nodeDTO.getIpAddress());
                    return nodeDetailDatasourceInfo;
                }).collect(Collectors.toList());
            }
            return Lists.newArrayList();
        }

        return null;
    }

    /**
     * 拓扑图异常的转换
     *
     * @param nodes 节点列表
     *
     * @return 拓扑图异常
     */
    private List<ExceptionListResponse> getExceptionsFromNodes(List<AbstractTopologyNodeResponse> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }

        // 未知节点异常
        List<ExceptionListResponse> unknownExceptions = nodes.stream().filter(this::isUnknownResponseNode)
                .map(node -> {
                    ExceptionListResponse exception = new ExceptionListResponse();
                    StringBuilder exceptionTitle = new StringBuilder();
                    exceptionTitle.append("节点\"")
                            .append(StringUtils.join(node.getUpAppNames(), ","))
                            .append("\"下游存在应用未接入探针");
                    if (CollectionUtils.isNotEmpty(node.getNodes())) {
                        exceptionTitle.append("，节点IP:")
                                .append(StringUtils.join(node.getNodes().stream()
                                        .map(NodeDetailDatasourceInfo::getNode)
                                        .collect(Collectors.toList()), ","));
                    }

                    exception.setTitle(exceptionTitle.toString());
                    exception.setType("应用未接入探针");
                    exception.setSuggest("确认该服务为外部服务或内部服务提供，若为外部服务请将其标记为外部服务；若为内部应用请将其接入探针");
                    return exception;
                }).collect(Collectors.toList());

        // 应用的中间件异常
        List<ExceptionListResponse> middlewareExceptions = this.getMiddlewareExceptionList(nodes);
        unknownExceptions.addAll(middlewareExceptions);
        return unknownExceptions;
    }

    /**
     * 拓扑图下的应用中间件异常判断
     * <p>
     * 遍历节点, 根据应用名查询 应用id
     * 根据应用id 查询其下所有中间件状态
     * 然后判断, 无, 未知, 未支持
     *
     * @param nodes 节点
     *
     * @return 中间件异常
     */
    private List<ExceptionListResponse> getMiddlewareExceptionList(List<AbstractTopologyNodeResponse> nodes) {
        // 收集所有的 应用名称
        List<String> applicationNameList = nodes.stream().flatMap(node -> node.getUpAppNames().stream())
                .distinct().collect(Collectors.toList());
        if (applicationNameList.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询出所有的 名称对应的中间件状态
        Map<String, Map<Integer, Integer>> applicationNameAboutStatusCountMap = applicationMiddlewareService
                .getApplicationNameAboutStatusCountMap(applicationNameList);
        if (applicationNameAboutStatusCountMap.isEmpty()) {
            return Collections.emptyList();
        }

        // 分隔处理
        int total = applicationNameList.size();
        int size = 500;
        int page = total / size + 1;

        // 从 0 开始, 但是是小于 page, 没有等于
        // 所以 页数的个数 是一样的
        // skip 从 0 开始, 0, 1; 2, 3; 4, 5; xxx
        return Stream.iterate(0, f -> f + 1).limit(page).parallel().flatMap(currentPage ->
            applicationNameList.stream().skip(currentPage * size).limit(size)
                .map(applicationName -> {
                    Map<Integer, Integer> statusAboutCount = applicationNameAboutStatusCountMap.get(applicationName);
                    if (statusAboutCount == null) {
                        return null;
                    }

                    ExceptionListResponse exception = new ExceptionListResponse();
                    exception.setTitle(String.format("应用: %s 存在不支持的中间件", applicationName));
                    exception.setType("应用中间件不支持");

                    StringBuilder sb = new StringBuilder();
                    sb.append("该应用存在 ");
                    if (statusAboutCount.get(ApplicationMiddlewareStatusEnum.NONE.getCode()) != null
                        && statusAboutCount.get(ApplicationMiddlewareStatusEnum.NONE.getCode()) > 0) {
                        sb.append("无状态 ");
                    }

                    if (statusAboutCount.get(ApplicationMiddlewareStatusEnum.UNKNOWN.getCode()) != null
                        && statusAboutCount.get(ApplicationMiddlewareStatusEnum.UNKNOWN.getCode()) > 0) {
                        sb.append("未知 ");
                    }

                    if (statusAboutCount.get(ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode()) != null
                        && statusAboutCount.get(ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode()) > 0) {
                        sb.append("未支持 ");
                    }

                    sb.append("的中间件, 请前往查看");
                    exception.setSuggest(sb.toString());
                    return exception;
                }).filter(Objects::nonNull).collect(Collectors.toList()).stream())
            .collect(Collectors.toList());
    }

    private List<ApplicationEntranceTopologyEdgeResponse> convertEdges(
            LinkTopologyDTO applicationEntrancesTopology,
            Map<String, LinkNodeDTO> nodeMap, Map<String, List<LinkEdgeDTO>> providerEdgeMap,
            Map<String, List<LinkEdgeDTO>> callEdgeMap, ApplicationEntranceTopologyQueryRequest request,
            ApplicationEntranceTopologyResponse applicationEntranceTopologyResponse) {

        if (CollectionUtils.isEmpty(applicationEntrancesTopology.getEdges())) {
            return Lists.newArrayList();
        }

        List<ApplicationEntranceTopologyEdgeResponse> edgeResponseList = applicationEntrancesTopology.getEdges()
                .stream()
                //.filter(linkEdgeDTO -> EdgeTypeEnum.getEdgeTypeEnum(linkEdgeDTO.getMiddlewareName()) != EdgeTypeEnum
                // .UNKNOWN)
                .map(edge -> {
                    ApplicationEntranceTopologyEdgeResponse
                            applicationEntranceTopologyEdgeResponse
                            = new ApplicationEntranceTopologyEdgeResponse();
                    // 虚拟入口边
                    if ("VIRTUAL".equals(edge.getEagleType())) {
                        //edge.setEagleType(request.getType().getType());
                        //edge.setEagleTypeGroup(EdgeTypeGroupEnum.getEdgeTypeEnum(request.getType().getType()).getType());
                        edge.setMethod(request.getMethod());
                        edge.setService(request.getServiceName());
                        edge.setMiddlewareName(request.getType().getType());
                    }
                    applicationEntranceTopologyEdgeResponse.setSource(edge.getSourceId());
                    applicationEntranceTopologyEdgeResponse.setTarget(edge.getTargetId());
                    //EdgeTypeEnum edgeTypeEnum = EdgeTypeEnum.getEdgeTypeEnum(edge.getMiddlewareName());
                    //applicationEntranceTopologyEdgeResponse.setLabel(edgeTypeEnum.name());
                    applicationEntranceTopologyEdgeResponse.setType(edge.getRpcType());
                    applicationEntranceTopologyEdgeResponse.setLabel(edge.getMiddlewareName());
                    //applicationEntranceTopologyEdgeResponse.setType(edgeTypeEnum.name());
                    applicationEntranceTopologyEdgeResponse.setInfo(convertEdgeInfo(edge, nodeMap));
                    applicationEntranceTopologyEdgeResponse.setId(edge.getEagleId() != null ? edge.getEagleId()
                            : MD5Util.getMD5(edge.getService() + "|" + edge.getMethod()));
                    return applicationEntranceTopologyEdgeResponse;
                }).collect(Collectors.toList());

        Map<String, Map<String, Set<String>>> collect = edgeResponseList.stream()
                .collect(
                        Collectors.groupingBy(ApplicationEntranceTopologyEdgeResponse::getSource,
                                Collectors.groupingBy(ApplicationEntranceTopologyEdgeResponse::getTarget,
                                        Collectors.mapping(ApplicationEntranceTopologyEdgeResponse::getInfo, Collectors.toSet()))));

        edgeResponseList
                .forEach(edge -> {
                    Map<String, Set<String>> stringListMap = collect.get(edge.getSource());
                    Set<String> strings = stringListMap.get(edge.getTarget());
                    edge.setInfo(null);
                    edge.setInfos(strings);
                });

        // 这里会将 Source --> Target 只保留一条边
        ArrayList<ApplicationEntranceTopologyEdgeResponse> collectResult =
                edgeResponseList.stream().collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing(this::fetchingGroupKey))),
                                ArrayList::new));

        return collectResult;
    }

    private <T> T convertNodeExtendInfo(Object extendInfo, Class<T> clazz) {
        String json = JSON.toJSONString(extendInfo);
        return JSON.parseObject(json, clazz);
    }

    private String convertEdgeInfo(LinkEdgeDTO edge, Map<String, LinkNodeDTO> nodeMap) {
        //if (EdgeTypeGroupEnum.HTTP.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_WEB_SERVER + "").equals(edge.getRpcType())) {
            return "请求方式：" + edge.getMethod() + "，请求路径：" + edge.getService();
        }
        //if (EdgeTypeGroupEnum.DUBBO.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_RPC + "").equals(edge.getRpcType())) {
            return "类：" + edge.getService() + "，方法(参数）：" + edge.getMethod();
        }
        //if (EdgeTypeGroupEnum.DB.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_DB + "").equals(edge.getRpcType())) {
            return "数据库：" + edge.getService() + ", 表名: " + edge.getMethod();
        }
        //if (EdgeTypeGroupEnum.MQ.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_MQ + "").equals(edge.getRpcType())) {
            // 消费者
            if (nodeMap.get(edge.getSourceId()).getNodeTypeGroup().equals(NodeTypeGroupEnum.MQ.getType())) {
                return "Topic：" + edge.getService() + "，Group：" + edge.getMethod();
            } else {
                return "Topic：" + edge.getService();
            }
        }
        //if (EdgeTypeGroupEnum.OSS.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_FS + "").equals(edge.getRpcType())) {
            return "URL：" + edge.getService();
        }
        //if (EdgeTypeGroupEnum.CACHE.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_CACHE + "").equals(edge.getRpcType())) {
            String db;
            String op;
            if (edge.getService().contains(":")) {
                String[] split = edge.getService().split(":");
                db = split[0];
                op = split[1];
            } else {
                db = edge.getService();
                op = edge.getMethod();
            }
            List<String> msg = new ArrayList<>();
            if (StringUtils.isNotBlank(db)) {
                msg.add("DB：" + db);
            }
            if (StringUtils.isNotBlank(op)) {
                msg.add("操作方式：" + op);
            }
            return StringUtils.join(msg, "，");
        }
        //if (EdgeTypeGroupEnum.JOB.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_JOB + "").equals(edge.getRpcType())) {
            return "类：" + edge.getService() + "，方法：" + edge.getMethod();
        } else if ((MiddlewareType.TYPE_SEARCH + "").equals(edge.getRpcType())) {
            return "Index：" + edge.getService() + "，操作方法：" + edge.getMethod();
        }
        return "";
    }

    private boolean isRoot(LinkNodeDTO node) {
        return node.isRoot() && node.getNodeName().endsWith("-Virtual");
    }

    private String getNodeInfoLabel(LinkNodeDTO node, AtomicLong nextNumber) {
        String label = node.getNodeName();
        if (isRoot(node)) {
            label = "入口";
        } else if (node.getNodeTypeGroup().equalsIgnoreCase(NodeTypeGroupEnum.APP.getType())
                || node.getNodeTypeGroup().equalsIgnoreCase(NodeTypeGroupEnum.CACHE.getType())) {
            label = node.getNodeName();
        }
        // 未知应用
        if (isUnknownNode(node)) {
            label = "未知应用" + nextNumber.incrementAndGet();
        }
        // 第三方服务
        else if (isOuterService(node)) {
            label = "外部应用" + nextNumber.incrementAndGet();
        }
        return label;
    }

    private boolean nodeIsApp(LinkNodeDTO node) {
        return node.getNodeType().equalsIgnoreCase(NodeTypeEnum.APP.name());
    }

    /**
     * 是否是未知应用
     * 根据节点名称判断
     *
     * @param node 节点实例
     * @return 是否是
     */
    private boolean isUnknownNode(LinkNodeDTO node) {
        return "UNKNOWN".equalsIgnoreCase(node.getNodeName());
    }

    private boolean isUnknownResponseNode(AbstractTopologyNodeResponse node) {
        return node.getNodeType().getType().equals(NodeTypeResponseEnum.UNKNOWN.getType());
    }

    /**
     * 外部应用
     * 根据节点名称判断
     *
     * @param node 节点实例
     * @return 是否是
     */
    private boolean isOuterService(LinkNodeDTO node) {
        return "OUTSERVICE".equalsIgnoreCase(node.getNodeName());
    }

    private boolean isVirtualNode(LinkNodeDTO node) {
        return node.getNodeName().endsWith("-Virtual");
    }

    private String fetchingGroupKey(ApplicationEntranceTopologyEdgeResponse response) {
        return response.getSource() + "@" + response.getTarget();
    }
}
