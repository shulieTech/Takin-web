package io.shulie.takin.web.biz.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.pradar.MiddlewareType;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.common.util.MD5Util;
import io.shulie.amdb.common.dto.link.topology.LinkEdgeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkNodeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.amdb.common.dto.link.topology.NodeExtendInfoBaseDTO;
import io.shulie.amdb.common.dto.link.topology.NodeExtendInfoForCacheDTO;
import io.shulie.amdb.common.dto.link.topology.NodeExtendInfoForDBDTO;
import io.shulie.amdb.common.dto.link.topology.NodeExtendInfoForMQDTO;
import io.shulie.amdb.common.dto.link.topology.NodeExtendInfoForOSSDTO;
import io.shulie.amdb.common.dto.link.topology.NodeExtendInfoForSearchDTO;
import io.shulie.amdb.common.enums.NodeTypeEnum;
import io.shulie.amdb.common.enums.NodeTypeGroupEnum;
import io.shulie.takin.cloud.ext.content.enums.RpcTypeEnum;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.QueryMetricsFromAMDB;
import io.shulie.takin.web.amdb.bean.query.application.TempTopologyQuery1;
import io.shulie.takin.web.amdb.bean.query.application.TempTopologyQuery2;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.AbstractTopologyNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.AppCallDatasourceInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.AppCallInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.AppProvider;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.AppProviderInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.ApplicationEntranceTopologyEdgeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.DbInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.ExceptionListResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.MqInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.NodeDetailDatasourceInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.NodeTypeResponseEnum;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.OssInfo;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyAppNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyCacheNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyDbNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyMqNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyOssNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyOtherNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologySearchNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyUnknownNodeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse.TopologyVirtualNodeResponse;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
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

/**
 * ??????????????? ??????--??????????????????AMDB??????
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

    public static final double INIT = 0.0; // db ????????????????????????

    /**
     * ???????????????, ??????
     *
     * @param request      ????????????
     * @param tempActivity ????????????????????????
     * @return ???????????????
     */
    public ApplicationEntranceTopologyResponse getApplicationEntrancesTopology(
        ApplicationEntranceTopologyQueryRequest request, boolean tempActivity) {

        // ????????????????????????
        LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
            tempActivity,
            request.getApplicationName(), request.getLinkId(), request.getServiceName(), request.getMethod(),
            request.getRpcType(), request.getExtend());

        // final result
        ApplicationEntranceTopologyResponse applicationEntranceTopologyResponse
            = new ApplicationEntranceTopologyResponse();

        // ?????? amdb ???????????????
        if (applicationEntrancesTopology == null) {
            applicationEntranceTopologyResponse.setNodes(Lists.newArrayList());
            applicationEntranceTopologyResponse.setEdges(Lists.newArrayList());
            applicationEntranceTopologyResponse.setExceptions(Lists.newArrayList());
            return applicationEntranceTopologyResponse;
        }

        // ???????????? amdb, ?????????
        processData(request, applicationEntrancesTopology, applicationEntranceTopologyResponse);

        return applicationEntranceTopologyResponse;
    }

    private void processData(
        ApplicationEntranceTopologyQueryRequest request,
        LinkTopologyDTO applicationEntrancesTopology,
        ApplicationEntranceTopologyResponse applicationEntranceTopologyResponse) {

        /* key:nodeId ,value: node */
        Map<String, LinkNodeDTO> nodeMap = Maps.newHashMap();
        /* key:nodeId ,value: ?????????node?????? */
        Map<String, List<LinkEdgeDTO>> providerEdgeMap = Maps.newHashMap();
        /* key:nodeId ,value: ???node???????????? */
        Map<String, List<LinkEdgeDTO>> callEdgeMap = Maps.newHashMap();
        /* key:applicationName ,value: managerName */
        Map<String, String> managerMap = Maps.newHashMap();
        /* key:applicationName ,value: ?????????????????? */
        Map<String, List<ApplicationNodeDTO>> appNodeMap = Maps.newHashMap();

        // nodes
        List<LinkNodeDTO> nodes = applicationEntrancesTopology.getNodes();
        if (CollectionUtils.isNotEmpty(nodes)) {
            nodeMap = nodes.stream().collect(Collectors.toMap(LinkNodeDTO::getNodeId, self -> self));
            managerMap = this.getManagers(nodes);
            appNodeMap = this.getAppNodes(nodes);
        }

        // edges
        List<LinkEdgeDTO> edges = applicationEntrancesTopology.getEdges();
        if (CollectionUtils.isNotEmpty(edges)) {
            providerEdgeMap = edges.stream().collect(Collectors.groupingBy(LinkEdgeDTO::getTargetId));
            callEdgeMap = edges.stream().collect(Collectors.groupingBy(LinkEdgeDTO::getSourceId));
        }

        // ????????????
        applicationEntranceTopologyResponse.setNodes(this.convertNodes(applicationEntrancesTopology, nodeMap,
            providerEdgeMap, callEdgeMap, managerMap, appNodeMap));
        // ??? ??????
        applicationEntranceTopologyResponse.setEdges(this.convertEdges(applicationEntrancesTopology, nodeMap,
            providerEdgeMap, callEdgeMap, request, applicationEntranceTopologyResponse));

        // ????????????
        applicationEntranceTopologyResponse.setExceptions(
            this.getExceptionsFromNodes(applicationEntranceTopologyResponse.getNodes()));
    }

    /**
     * @param startTimeUseInInFluxDB                  ???????????? ????????????
     * @param endTimeUseInInFluxDB                    ???????????? ????????????
     * @param allTotalCountStartDateTimeUseInInFluxDB ???????????? ??????????????????????????? ????????????
     */
    public void fillMetrics(ActivityInfoQueryRequest request,
        ApplicationEntranceTopologyResponse topologyResponse,
        LocalDateTime startTimeUseInInFluxDB,
        LocalDateTime endTimeUseInInFluxDB,
        LocalDateTime allTotalCountStartDateTimeUseInInFluxDB) {

        Boolean metricsType = null;
        // ????????????(true)???????????????(false)???????????????(null)
        if (FlowTypeEnum.PRESSURE_MEASUREMENT.equals(request.getFlowTypeEnum())) {
            metricsType = true;
        } else if (FlowTypeEnum.BUSINESS.equals(request.getFlowTypeEnum())) {
            metricsType = false;
        }

        // startTime
        long startMilliUseInInFluxDB = startTimeUseInInFluxDB.toInstant(ZoneOffset.of("+0")).toEpochMilli();
        long allTotalCountStartMilliUseInInFluxDB = allTotalCountStartDateTimeUseInInFluxDB.toInstant(
            ZoneOffset.of("+0")).toEpochMilli();

        // endTime
        long endMilliUseInInFluxDB = endTimeUseInInFluxDB.toInstant(ZoneOffset.of("+0")).toEpochMilli();

        /*
        ?????? Node
            ???Tps / ???Rt
        */

        // ?????? ??????????????? ?????????????????????
        List<ActivityNodeState> dbActivityNodeServiceState = activityService.getActivityNodeServiceState(
            request.getActivityId());

        List<ApplicationEntranceTopologyEdgeResponse> reduceEdges = topologyResponse.getEdges();
        List<AbstractTopologyNodeResponse> allNodes = topologyResponse.getNodes();

        // ?????? root ??????
        final AbstractTopologyNodeResponse rootNode = allNodes.stream()
            .filter(AbstractTopologyNodeResponse::getRoot)
            .findFirst().get();

        // ?????? ????????????????????????????????????????????????
        String response1 = "";
        response1 = getString(request, rootNode, response1, reduceEdges, allNodes);

        // ????????????????????????
        Map<String, List<E2eExceptionConfigInfoExt>> bottleneckConfigMap = this.getBatchExceptionConfig(allNodes);

        // ????????????????????????
        Map<String, JSONObject> metricsMap = Maps.newHashMap();
        if (!request.isTempActivity()) {
            metricsMap = queryBatchMetricsFromAMDB(startMilliUseInInFluxDB, endMilliUseInInFluxDB,
                metricsType, this.getAllEagleIds(allNodes));
        }

        for (AbstractTopologyNodeResponse node : allNodes) {
            // ?????? ??????????????? ???????????? / ???????????? / ???Tps / ???Rt
            TopologyAppNodeResponse appnode = (TopologyAppNodeResponse)node;
            if (appnode.getProviderService() != null) {
                List<AppProviderInfo> appProviderInfos =
                    fillAppNodeServiceSuccessRateAndRt(
                        request, appnode, startTimeUseInInFluxDB, bottleneckConfigMap,
                        dbActivityNodeServiceState, response1, metricsMap);

                // ?????? ??????????????????????????????????????????????????????
                setTopologyNodeServiceMetrics(node, appProviderInfos);
            }
        }

        // ?????????????????????????????????
        setTopologyLevelBottleneck(topologyResponse);

        /*
        ?????? Edge
            ???????????? / ??????
        */
        for (ApplicationEntranceTopologyEdgeResponse edge : reduceEdges) {
            edge.setAllTotalCount(getAllServiceAllTotalCount(allNodes, edge));

            edge.setMain(false);
        }

        /*
        ?????? ??????
        */
        if (rootNode == null) {
            log.info("no entrance node");
            return;
        }
        int loopCounter = 0;
        setMainEdge(reduceEdges, rootNode.getId(), loopCounter);
    }

    /**
     * ??????????????????????????????-????????????-??????????????????????????????
     *
     * @param allNodes
     * @return
     */
    private Map<String, List<E2eExceptionConfigInfoExt>> getBatchExceptionConfig(
        List<AbstractTopologyNodeResponse> allNodes) {
        List<String> allNodeService = new ArrayList<>();
        for (AbstractTopologyNodeResponse node : allNodes) {
            TopologyAppNodeResponse appnode = (TopologyAppNodeResponse)node;
            if (appnode.getProviderService() != null) {
                List<AppProviderInfo> providerService = node.getProviderService();
                for (AppProviderInfo appProviderInfo : providerService) {
                    for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                        for (LinkEdgeDTO linkEdgeDTO : appProvider.getContainEdgeList()) {
                            String service = appProvider.getOwnerApps() + "#" + appProvider.getServiceName() + "#"
                                + linkEdgeDTO.getRpcType();
                            allNodeService.add(service);
                        }
                    }
                }
            }
        }
        return this.doGetServiceExceptionConfig(
            allNodeService);
    }

    /**
     * ???????????????????????????ID
     *
     * @param allNodes
     * @return
     */
    private List<String> getAllEagleIds(List<AbstractTopologyNodeResponse> allNodes) {
        List<String> allEagleIds = new ArrayList<>();
        for (AbstractTopologyNodeResponse node : allNodes) {
            TopologyAppNodeResponse appnode = (TopologyAppNodeResponse)node;
            if (appnode.getProviderService() != null) {
                List<AppProviderInfo> providerService = node.getProviderService();
                for (AppProviderInfo appProviderInfo : providerService) {
                    for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                        List<String> eagleIds = appProvider.getContainEdgeList().stream().map(LinkEdgeDTO::getEagleId)
                            .collect(
                                Collectors.toList());
                        if (CollectionUtils.isNotEmpty(eagleIds)) {
                            allEagleIds.addAll(eagleIds);
                        }
                    }
                }
            }
        }
        return allEagleIds;
    }

    private String getString(ActivityInfoQueryRequest request, AbstractTopologyNodeResponse rootNode, String response1,
        List<ApplicationEntranceTopologyEdgeResponse> reduceEdges, List<AbstractTopologyNodeResponse> allNodes) {
        if (request.isTempActivity()) {

            // ?????? ?????? root ????????????
            ApplicationEntranceTopologyEdgeResponse rootEdge = reduceEdges.stream()
                .filter(s -> s.getSource().equals(rootNode.getId()))
                .findFirst().get();

            // ?????? ????????? linkEdgeDTO
            for (AbstractTopologyNodeResponse node : allNodes) {
                TopologyAppNodeResponse appnode = (TopologyAppNodeResponse)node;
                if (appnode.getProviderService() != null) {
                    for (AppProviderInfo appProviderInfo : node.getProviderService()) {
                        for (AppProvider appProvider : appProviderInfo.getDataSource()) {

                            for (LinkEdgeDTO linkEdgeDTO : appProvider.getContainEdgeList()) {
                                String eagleId = linkEdgeDTO.getEagleId();

                                if (!rootEdge.getId().equals(eagleId)) { continue; }

                                String serviceName = appProvider.getServiceName();
                                String[] split = serviceName.split("#");
                                String service = split[0];
                                String method = split[1];

                                // step 1
                                String startTime = DateUtils.formatLocalDateTime(request.getStartTime());
                                String endTime = DateUtils.formatLocalDateTime(request.getEndTime());

                                TempTopologyQuery1 query1 = TempTopologyQuery1.builder()
                                    .inAppName(appProvider.getOwnerApps())
                                    .inService(service)
                                    .inMethod(method)
                                    .startTime(startTime)
                                    .endTime(endTime)
                                    .timeGap(request.getTimeGap())
                                    .build();

                                response1 = applicationEntranceClient.queryMetricsFromAMDB1(query1);
                            }
                        }
                    }
                }
            }
        }
        return response1;
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

        // edge ????????????
        for (AbstractTopologyNodeResponse node : allNodes) {
            if (edge.getTarget().equals(node.getId())) {
                // ?????? ?????? ????????????
                List<AppProviderInfo> providerService = node.getProviderService();
                if (providerService == null) {
                    continue;
                }

                for (AppProviderInfo appProviderInfo : providerService) {
                    for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                        for (AppProvider realProvider : appProvider.getContainRealAppProvider()) {
                            if ((realProvider.getSource().equals(edge.getSource()) &&
                                realProvider.getTarget().equals(edge.getTarget()))) {
                                allServiceAllTotalCount = bigDecimalAdd(allServiceAllTotalCount,
                                    realProvider.getServiceAllTotalCount());
                            }
                        }
                    }
                }
            }
        }
        return allServiceAllTotalCount;
    }

    private void setTopologyNodeServiceMetrics(AbstractTopologyNodeResponse node,
        List<AppProviderInfo> appProviderInfos) {
        AppProvider appProviderContainer = AppProvider.buildInit();

        for (AppProviderInfo appProviderInfo : appProviderInfos) {
            List<AppProvider> appProviderList = appProviderInfo.getDataSource();
            avgComputer(appProviderContainer, appProviderList);
        }

        // ??? node ?????????
        node.setServiceAllTotalCount(appProviderContainer.getServiceAllTotalCount());
        node.setServiceAllSuccessRate(appProviderContainer.getServiceAllSuccessRate());
        node.setServiceAllTotalTps(appProviderContainer.getServiceAllTotalTps());
        node.setServiceRt(appProviderContainer.getServiceRt());
    }

    private AppProvider avgComputer(AppProvider appProviderContainer, List<AppProvider> appProviderList) {
        appProviderList.stream()
            // ?????????????????????????????????
            .filter(a -> {
                if (a.getSwitchState() == null) { return true; } else { return a.getSwitchState(); }
            })
            .forEach(appProvider -> {
                appProviderContainer.setServiceAllTotalCount(
                    bigDecimalAdd(appProviderContainer.getServiceAllTotalCount(), appProvider.getServiceAllTotalCount())
                );
                appProviderContainer.setAllSuccessCount(
                    bigDecimalAdd(appProviderContainer.getAllSuccessCount(), appProvider.getAllSuccessCount())
                );
                appProviderContainer.setServiceAllTotalTps(
                    bigDecimalAdd(appProviderContainer.getServiceAllTotalTps(), appProvider.getServiceAllTotalTps())
                );
                appProviderContainer.setAllTotalRt(
                    bigDecimalAdd(appProviderContainer.getAllTotalRt(), appProvider.getAllTotalRt())
                );
                appProviderContainer.setServiceAllMaxRt(
                    Math.max(appProviderContainer.getServiceAllMaxRt(), appProvider.getServiceAllMaxRt())
                );
            });

        appProviderContainer.setServiceAllSuccessRate(
            bigDecimalDivide(appProviderContainer.getAllSuccessCount(), appProviderContainer.getServiceAllTotalCount())
        );
        appProviderContainer.setServiceRt(
            bigDecimalDivide(appProviderContainer.getAllTotalRt(), appProviderContainer.getServiceAllTotalCount())
        );

        return appProviderContainer;
    }

    private List<AppProviderInfo> fillAppNodeServiceSuccessRateAndRt(
        ActivityInfoQueryRequest request, TopologyAppNodeResponse node,
        LocalDateTime startTimeUseInInFluxDB, Map<String, List<E2eExceptionConfigInfoExt>> bottleneckConfigMap,
        List<ActivityNodeState> dbActivityNodeServiceState, String response1, Map<String, JSONObject> metricsMap) {

        // ?????? ???????????????????????? ?????????????????????????????? serviceMethod
        List<AppProvider> allAppProviderServiceList = new ArrayList<>();

        List<AppProviderInfo> providerService = node.getProviderService();
        for (AppProviderInfo appProviderInfo : providerService) {
            for (AppProvider appProvider : appProviderInfo.getDataSource()) {
                // ????????????????????????????????????????????????
                fillMetrixFromAMDB(request, appProvider, response1, metricsMap);

                // ?????? ???????????? ????????????
                mergeSameBeforeApp(appProvider);

                // ????????????
                appProvider.getContainRealAppProvider().stream()
                    // ???????????????????????????????????????
                    .filter(appProviderFromDb -> !appProviderFromDb.getServiceAllTotalCount().equals(INIT))
                    .forEach(appProviderFromDb -> {
                        // ???????????? and ??????
                        computeBottleneck(startTimeUseInInFluxDB, request.getActivityId(), bottleneckConfigMap,
                            appProviderFromDb);
                    });

                // ???????????????????????????????????????????????????????????????????????????????????????????????????, ??????????????????????????????????????????
                // ??????API?????????API?????????????????????????????????
                fillNodeServiceMetrics(appProvider);

                //?????????????????????API????????????????????????
                allAppProviderServiceList.add(appProvider);
                appProvider.setBeforeAppsMap(null);
            }
        }

        if (!allAppProviderServiceList.isEmpty()) {
            // ?????? ??? ?????? ???????????? ????????????
            setNodeServiceState(request.getActivityId(), node, dbActivityNodeServiceState, allAppProviderServiceList);
            // ?????? ???????????????????????????
            setNodeBottleneck(node, allAppProviderServiceList);
        }

        return providerService;
    }

    public Map<String, List<E2eExceptionConfigInfoExt>> doGetServiceExceptionConfig(List<String> services) {
        return E2ePluginUtils.getBatchExceptionConfig(
            WebPluginUtils.traceTenantId(),
            WebPluginUtils.traceEnvCode(), services);
    }

    private void mergeSameBeforeApp(AppProvider appProvider) {
        Map<String, AppProvider> appProviderHashMap = appProvider.getContainRealAppProvider().stream()
            .collect(Collectors.toMap(
                AppProvider::getBeforeApps,
                Function.identity(),
                (existingValue, newValue) -> {
                    ArrayList<AppProvider> arrayList = new ArrayList();
                    arrayList.add(newValue);

                    avgComputer(existingValue, arrayList);

                    existingValue.setEagleId(existingValue.getEagleId() + "," + newValue.getEagleId());
                    return existingValue;
                }));

        appProvider.setContainRealAppProvider(new ArrayList(appProviderHashMap.values()));
    }

    private void fillMetrixFromAMDB(ActivityInfoQueryRequest request, AppProvider appProvider, String response1,
        Map<String, JSONObject> metricsMap) {
        appProvider.setContainRealAppProvider(new ArrayList<>());

        HashMap<String, Boolean> isContainSame = new HashMap<>();
        for (LinkEdgeDTO linkEdgeDTO : appProvider.getContainEdgeList()) {
            String eagleId = linkEdgeDTO.getEagleId();
            String beforeApps = appProvider.getBeforeAppsMap().get(linkEdgeDTO.getSourceId());

            // ??????????????????????????????
            AppProvider appProviderFromDb = new AppProvider();
            if (request.isTempActivity()) {
                // ????????????->????????????  ?????? ????????????????????? ??????
                appProviderFromDb = queryTempMetricsFromAMDB(response1, isContainSame, beforeApps,
                    appProvider.getOwnerApps(), appProvider.getMiddlewareName(), appProvider.getServiceName(), request);
            } else {
                if (metricsMap.containsKey(eagleId)) {
                    appProviderFromDb = queryMetricsFromAMDB(metricsMap.get(eagleId));
                }
            }
            appProviderFromDb.setBeforeApps(beforeApps);
            appProviderFromDb.setOwnerApps(appProvider.getOwnerApps());
            appProviderFromDb.setEagleId(eagleId);
            appProviderFromDb.setSource(linkEdgeDTO.getSourceId());
            appProviderFromDb.setTarget(linkEdgeDTO.getTargetId());
            appProviderFromDb.setRpcType(linkEdgeDTO.getRpcType());
            appProviderFromDb.setServiceName(appProvider.getServiceName());
            appProviderFromDb.setMiddlewareName(appProvider.getMiddlewareName());

            // ???????????? init
            int rateBottleneckType = -1; // ????????????(-1 ????????????)
            appProviderFromDb.setAllSuccessRateBottleneckType(rateBottleneckType);
            appProviderFromDb.setAllTotalRtBottleneckType(rateBottleneckType);
            appProviderFromDb.setAllSqlTotalRtBottleneckType(rateBottleneckType);

            appProvider.getContainRealAppProvider().add(appProviderFromDb);
        }
    }

    private AppProvider queryTempMetricsFromAMDB(
        String response1, HashMap<String, Boolean> isContainSame, String beforeApps, String toAppName,
        String middlewareName, String serviceName, ActivityInfoQueryRequest request) {

        String[] split = serviceName.split("#");
        String service = split[0];
        String method = split[1];

        // ?????? ??? key ????????????????????????????????????
        String key = beforeApps + toAppName + middlewareName + service + method;
        Boolean aBoolean = isContainSame.get(key);
        if (null == aBoolean) {
            isContainSame.put(key, true);
        } else {
            // ??????????????????
            return getAppProvider(0L, Collections.emptyList());
        }

        String startTime = DateUtils.formatLocalDateTime(request.getStartTime());
        String endTime = DateUtils.formatLocalDateTime(request.getEndTime());

        // step 2
        ArrayList<TraceMetricsResult> traceMetricsResultList = new ArrayList<>();
        Integer realSeconds = 0;

        TempTopologyQuery2 query2 = TempTopologyQuery2.builder()
            .fromAppName(beforeApps)
            .appName(toAppName)
            .middlewareName(middlewareName)
            .service(service)
            .method(method)
            .entranceStr(response1)
            .clusterTest(request.getFlowTypeEnum().getType())
            .startTime(startTime)
            .endTime(endTime)
            .timeGap(request.getTimeGap())
            .build();

        JSONObject jsonObject = applicationEntranceClient.queryMetricsFromAMDB2(query2);

        fillTraceMetricsResultList(traceMetricsResultList, jsonObject);

        realSeconds = (Integer)jsonObject.get("realSeconds");

        AppProvider appProvider = getAppProvider(realSeconds, traceMetricsResultList);
        return appProvider;
    }

    private void fillTraceMetricsResultList(ArrayList<TraceMetricsResult> traceMetricsResultList,
        JSONObject jsonObject) {
        TraceMetricsResult traceMetricsResult = TraceMetricsResult.builder().build();

        Integer allTotalCount = (Integer)jsonObject.get("allTotalCount");
        traceMetricsResult.setAllTotalCount(allTotalCount.doubleValue());

        Integer allSuccessCount = (Integer)jsonObject.get("allSuccessCount");
        traceMetricsResult.setAllSuccessCount(allSuccessCount.doubleValue());

        Integer allTotalRt = (Integer)jsonObject.get("allTotalRt");
        traceMetricsResult.setAllTotalRt(allTotalRt.doubleValue());

        Integer allMaxRt = (Integer)jsonObject.get("allMaxRt");
        traceMetricsResult.setAllMaxRt(allMaxRt.doubleValue());

        traceMetricsResultList.add(traceMetricsResult);
    }

    private void setNodeBottleneck(TopologyAppNodeResponse node, List<AppProvider> allAppProviderServiceList) {
        // ???????????? [false ???????????? | true ???????????????]
        boolean hasL1Bottleneck = false;
        // ??????????????????
        int L1bottleneckNum = 0;

        // ???????????? [false ???????????? | true ???????????????]
        boolean hasL2Bottleneck = false;
        // ??????????????????
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
        AppProvider appProviderContainer = AppProvider.buildInit();

        // ??????????????? ????????????????????? ???????????????
        avgComputer(appProviderContainer, appProvider.getContainRealAppProvider());

        // ????????? ??????????????? 4 ???????????????

        // 1) ????????????
        appProvider.setServiceAllTotalCount(appProviderContainer.getServiceAllTotalCount());

        // ??????????????????1+??????2+??????3 ???????????????/?????????1+??????2+??????3 ????????????
        appProvider.setAllSuccessCount(appProviderContainer.getAllSuccessCount());
        // 2) ????????????
        appProvider.setServiceAllSuccessRate(appProviderContainer.getServiceAllSuccessRate());

        // 3) ???Tps          tps ??????1+??????2+??????3 tps
        appProvider.setServiceAllTotalTps(appProviderContainer.getServiceAllTotalTps());

        // rt ?????????1+??????2+??????3 ???????????? /?????????1+??????2+??????3 ????????????
        appProvider.setAllTotalRt(appProviderContainer.getAllTotalRt());
        appProvider.setServiceAvgRt(appProviderContainer.getServiceRt());
        appProvider.setServiceAllMaxRt(appProviderContainer.getServiceAllMaxRt());
        // 4) ???????????? RT
        appProvider.setServiceRt(appProvider.getServiceAvgRt());
    }

    public void computeBottleneck(
        LocalDateTime startTimeUseInInFluxDB, Long activityId,
        Map<String, List<E2eExceptionConfigInfoExt>> bottleneckConfigMap, AppProvider appProvider) {

        E2eStorageRequest storageRequest = new E2eStorageRequest();
        storageRequest.setRt(appProvider.getServiceAvgRt());
        storageRequest.setMaxRt(appProvider.getServiceAvgRt()); // SLOW_SQL ???
        storageRequest.setSuccessRate(appProvider.getServiceAllSuccessRate());

        //SY:????????????????????????
        //????????????????????????
        String service = appProvider.getOwnerApps() + "#" + appProvider.getServiceName() + "#"
            + appProvider.getRpcType();
        List<E2eExceptionConfigInfoExt> bottleneckConfig = bottleneckConfigMap.get(service);
        Map<Integer, Integer> resultMap = E2ePluginUtils.bottleneckCompute(storageRequest, bottleneckConfig);

        // ???????????? ?????????
        if (resultMap.isEmpty()) {
            return;
        }

        // ????????????????????????
        E2eBaseStorageParam baseStorageParam = new E2eBaseStorageParam();
        baseStorageParam.setSuccessRate(appProvider.getServiceAllSuccessRate());
        baseStorageParam.setRt(appProvider.getServiceAvgRt()); // ??????
        baseStorageParam.setStartTime(DateUtils.convertLocalDateTimeToUDate(startTimeUseInInFluxDB.plusHours(8)));
        baseStorageParam.setServiceName(appProvider.getOwnerApps() + "#" + appProvider.getServiceName());
        baseStorageParam.setRpcType(appProvider.getRpcType());
        // ????????????????????????????????????????????????
        baseStorageParam.setEdgeId(appProvider.getEagleId());
        baseStorageParam.setActivityId(activityId);
        // ????????????
        baseStorageParam.setTenantId(WebPluginUtils.traceTenantId());
        baseStorageParam.setEnvCode(WebPluginUtils.traceEnvCode());
        baseStorageParam.setUserId(WebPluginUtils.traceUserId());

        // ?????? rt
        if (RpcTypeEnum.APP.getValue().equals(baseStorageParam.getRpcType())) {
            Integer rtInt = resultMap.get(1);
            if (rtInt != null) {
                baseStorageParam.setType(1);
                baseStorageParam.setLevel(rtInt);

                // ??? ???????????????
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

            // ??? ???????????????
            Long storage = E2ePluginUtils.bottleneckStorage(baseStorageParam);
            appProvider.setSuccessRateBottleneckId(storage);
            appProvider.setAllSuccessRateBottleneckType(successRateInt);
        }

        // ???SQL ??????????????????maxRt
        if (RpcTypeEnum.DB.getValue().equals(baseStorageParam.getRpcType())) {
            Integer DbRtInt = resultMap.get(4);
            if (DbRtInt != null) {
                baseStorageParam.setType(4);
                baseStorageParam.setLevel(DbRtInt);

                // ??? ???????????????
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

        // ?????????????????????????????????
        List<ActivityNodeState> currentNodeStateList = new ArrayList<>();
        for (ActivityNodeState activityNodeState : dbActivityNodeServiceState) {
            if (nodeLabel.equals(activityNodeState.getOwnerApp())) {
                currentNodeStateList.add(activityNodeState);
            }
        }

        // ?????????????????????
        for (AppProvider appProvider : appProviderList) {
            String nodeServiceName = appProvider.getMiddlewareName() + ":" + appProvider.getServiceName();

            for (ActivityNodeState dbCurrentNodeState : currentNodeStateList) {
                // ????????? node ??? db ???, ???????????????, ?????? db ????????????
                if (dbCurrentNodeState.getServiceName().equals(nodeServiceName)) {
                    appProvider.setSwitchState(dbCurrentNodeState.isState());
                }
            }

            // ????????????????????????????????????????????? db ?????? ?????????????????????????????? ????????????
            if (appProvider.getSwitchState() == null) {
                activityService.setActivityNodeState(activityId, nodeLabel, nodeServiceName, true);
                appProvider.setSwitchState(true);
            }
        }
    }

    private AppProvider queryMetricsFromAMDB(long startMilli, long endMilli,
        Boolean metricsType, String eagleId) {

        QueryMetricsFromAMDB queryMetricsFromAMDB = QueryMetricsFromAMDB.builder()
            .startMilli(startMilli)
            .endMilli(endMilli)
            .metricsType(metricsType)
            .eagleId(eagleId)
            .tenantAppKey(WebPluginUtils.traceTenantAppKey())
            .envCode(WebPluginUtils.traceEnvCode())
            .build();

        JSONObject jsonObject = applicationEntranceClient.queryMetrics(queryMetricsFromAMDB);

        ArrayList<TraceMetricsResult> allTotalTpsAndRtCountResults = new ArrayList<>();
        fillTraceMetricsResultList(allTotalTpsAndRtCountResults, jsonObject);

        long realSeconds = (Integer)jsonObject.get("realSeconds");
        AppProvider appProvider = getAppProvider(realSeconds, allTotalTpsAndRtCountResults);
        return appProvider;
    }

    private AppProvider queryMetricsFromAMDB(JSONObject jsonObject) {
        ArrayList<TraceMetricsResult> allTotalTpsAndRtCountResults = new ArrayList<>();
        fillTraceMetricsResultList(allTotalTpsAndRtCountResults, jsonObject);

        long realSeconds = (Integer)jsonObject.get("realSeconds");
        AppProvider appProvider = getAppProvider(realSeconds, allTotalTpsAndRtCountResults);
        return appProvider;
    }

    private Map<String, JSONObject> queryBatchMetricsFromAMDB(Long startMilli, Long endMilli,
        Boolean metricsType, List<String> eagleIds) {

        if (CollectionUtils.isEmpty(eagleIds)) {
            return Maps.newHashMap();
        }

        QueryMetricsFromAMDB queryMetricsFromAMDB = QueryMetricsFromAMDB.builder()
            .startMilli(startMilli)
            .endMilli(endMilli)
            // ????????????(true:1)???????????????(false:0)???????????????(null:-1)
            .metricsType(metricsType)
            .eagleIds(eagleIds)
            .tenantAppKey(WebPluginUtils.traceTenantAppKey())
            .envCode(WebPluginUtils.traceEnvCode())
            .build();

        List<JSONObject> jsonObjects = applicationEntranceClient.queryBatchMetrics(queryMetricsFromAMDB);
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Maps.newHashMap();
        }
        Map<String, JSONObject> metricsMap = jsonObjects.stream().collect(
            Collectors.toMap(jsonObject -> String.valueOf(jsonObject.get("edgeId")), self -> self));
        return metricsMap;
    }

    public AppProvider getAppProvider(long realSeconds, List<TraceMetricsResult> allTotalTpsAndRtCountResults) {
        AppProvider appProvider = new AppProvider();

        // ?????? ?????????
        appProvider.setServiceAllTotalCount(INIT); // ????????????
        appProvider.setServiceAllSuccessRate(INIT); // ????????????
        appProvider.setAllSuccessCount(INIT); // ?????????????????????
        appProvider.setServiceAllTotalTps(INIT); // ???Tps

        appProvider.setServiceRt(INIT); // ???Rt
        appProvider.setServiceAvgRt(INIT); // ??????Rt
        appProvider.setAllTotalRt(INIT); // ?????????Rt
        appProvider.setServiceAllMaxRt(INIT); // maxRt

        if (allTotalTpsAndRtCountResults.size() != 0) {
            TraceMetricsResult traceMetricsResult = allTotalTpsAndRtCountResults.get(0);

            // 1)
            // ????????????
            Double allTotalCount = traceMetricsResult.getAllTotalCount();
            appProvider.setServiceAllTotalCount(allTotalCount);

            // 2)
            // ?????????????????????
            appProvider.setAllSuccessCount(traceMetricsResult.getAllSuccessCount());
            // ????????????     ????????????????????? / ???????????????    SUM(successCount) / SUM(totalCount)
            double allSuccessCountRate = bigDecimalDivide(appProvider.getAllSuccessCount(), allTotalCount);
            appProvider.setServiceAllSuccessRate(allSuccessCountRate);

            // 3)
            // ??????Tps    ??????????????? / ?????????    SUM(totalCount) / ?????????
            double allTotalTps = bigDecimalDivide(allTotalCount, new Long(realSeconds).doubleValue());
            appProvider.setServiceAllTotalTps(allTotalTps);

            // 4)
            // ?????????Rt
            appProvider.setAllTotalRt(traceMetricsResult.getAllTotalRt());
            // ?????? Rt    ?????????Rt / ???????????????    SUM(totalRt) / SUM(totalCount)
            double avgRt = bigDecimalDivide(appProvider.getAllTotalRt(), allTotalCount);
            appProvider.setServiceAvgRt(avgRt);
            appProvider.setServiceRt(appProvider.getServiceAvgRt());
            // ?????? Rt    MAX(maxRt)
            double allMaxRt = traceMetricsResult.getAllMaxRt();
            appProvider.setServiceAllMaxRt(allMaxRt);
        }
        return appProvider;
    }

    private void setMainEdge(
        List<ApplicationEntranceTopologyEdgeResponse> allEdges,
        String node,
        int loopCounter) {

        // ?????? (from / to) ????????? node, ??????????????????
        if (loopCounter++ > allEdges.size()) {
            return;
        }

        // ?????? node ??????????????? ????????? edgeListOfNode
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

        // ???????????? ??????????????????????????????
        if (edgeListOfNode.size() == 0) {
            return;
        }

        // ?????? ??????????????? ???????????? ???????????????
        // ???????????? ????????? ?????? 0??? ????????????????????????
        int maxTotalCountIndex = 0;
        Double maxAllTotalCount = 0D;
        for (int i = 0; i < edgeListOfNode.size(); i++) {
            Double currentAllTotalCount = edgeListOfNode.get(i).getAllTotalCount();
            if (currentAllTotalCount > maxAllTotalCount) {
                maxAllTotalCount = currentAllTotalCount;
                maxTotalCountIndex = i;
            }
        }

        // ???????????? ??????????????? ?????? ??????
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
        List<String> applicationNames = nodes.stream()
            .filter(node -> nodeIsApp(node) && !isVirtualNode(node))
            .map(LinkNodeDTO::getNodeName)
            .collect(Collectors.toList());

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
                    setVirtualResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap,
                        appNodeMap);
                    return nodeResponse;
                } else if (isOuterService(node)) {
                    TopologyOtherNodeResponse nodeResponse = new TopologyOtherNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setOtherResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                    return nodeResponse;
                } else if (isUnknownNode(node)) {
                    TopologyUnknownNodeResponse nodeResponse = new TopologyUnknownNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setUnknownResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap,
                        appNodeMap);
                    return nodeResponse;
                } else {
                    TopologyOtherNodeResponse nodeResponse = new TopologyOtherNodeResponse();
                    setNodeDefaultResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, nextNumber);
                    setOtherResponse(nodeResponse, node, nodeMap, providerEdgeMap, callEdgeMap, managerMap, appNodeMap);
                    return nodeResponse;
                }

                // ??????????????????
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
     * ??????????????????????????????
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
        //SY:?????????MQ???????????????????????????????????????????????????????????????
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

        // ?????????????????????
        List<LinkEdgeDTO> providerEdge = providerEdgeMap.get(node.getNodeId());
        if (CollectionUtils.isEmpty(providerEdge)) {
            return Lists.newArrayList();
        }

        // ?????? ????????? ??????
        Map<String, List<LinkEdgeDTO>> providerEdgeByMiddlewareName = providerEdge.stream().collect(
            Collectors.groupingBy(LinkEdgeDTO::getMiddlewareName));

        List<AppProviderInfo> collect = providerEdgeByMiddlewareName.entrySet().stream().map(edgeByMiddlewareName -> {
            AppProviderInfo appProviderInfo = new AppProviderInfo();
            appProviderInfo.setLabel(edgeByMiddlewareName.getKey()); // MiddlewareName

            List<AppProvider> datasource = this.getDatasource(node, nodeMap, edgeByMiddlewareName, Lists.newArrayList());
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

        // ?????? ServiceMethod ????????????
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

                List<String> keyList = item.getValue().stream()
                    .filter(Objects::nonNull)
                    .map(LinkEdgeDTO::getSourceId)
                    .collect(Collectors.toList());

                for (String id : keyList) {
                    LinkNodeDTO linkNodeDTO = nodeMap.get(id);
                    if (linkNodeDTO != null) {
                        String nodeName = linkNodeDTO.getNodeName();
                        sourceIdToNodeName.put(id, nodeName);
                    }
                }

                // ??????????????????(?????????????????????)
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
        }).distinct().collect(Collectors.toList());

        return datasource;
    }

    private String getServiceMethod(LinkEdgeDTO e) {
        return e.getService() + "#" + e.getMethod();
    }

    private List<AppCallInfo> convertAppNodeCallService(LinkNodeDTO node,
        Map<String, LinkNodeDTO> nodeMap,
        Map<String, List<LinkEdgeDTO>> providerEdgeMap, Map<String, List<LinkEdgeDTO>> callEdgeMap,
        Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        // ?????????????????????
        // (??????????????????)
        List<LinkEdgeDTO> callEdges = callEdgeMap.get(node.getNodeId());
        if (CollectionUtils.isEmpty(callEdges)) {
            return Lists.newArrayList();
        }
        Map<String, List<LinkEdgeDTO>> excludeMqNodeWithEdge = callEdges.stream().collect(
            Collectors.groupingBy(LinkEdgeDTO::getTargetId));
        return getCallInfo(nodeMap, excludeMqNodeWithEdge, appNodeMap);
    }

    public List<AppCallInfo> getCallInfo(Map<String, LinkNodeDTO> nodeMap,
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
            info1.setLabel("????????????");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);

            AppCallDatasourceInfo info2
                = new AppCallDatasourceInfo();
            info2.setLabel("??????");
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
            info3.setLabel("??????");
            info3.setDataSource(
                edges.stream().map(this::getServiceMethod).distinct().collect(Collectors.toList()));
            infos.add(info3);

            return infos;
        }

        if (NodeTypeGroupEnum.CACHE.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info1
                = new AppCallDatasourceInfo();
            info1.setLabel("??????");
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
            info2.setLabel("????????????");
            info2.setDataSource(edges.stream().map(LinkEdgeDTO::getService).collect(Collectors.toList()));
            infos.add(info2);

            return infos;
        }
        if (NodeTypeGroupEnum.MQ.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();

            AppCallDatasourceInfo info1
                = new AppCallDatasourceInfo();
            info1.setLabel("????????????");
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
            info1.setLabel("????????????");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);

            AppCallDatasourceInfo info2
                = new AppCallDatasourceInfo();
            info2.setLabel("?????????");
            info2.setDataSource(Lists.newArrayList(edges.stream().map(LinkEdgeDTO::getService).distinct()
                .collect(Collectors.joining(","))));
            infos.add(info2);

            AppCallDatasourceInfo info3
                = new AppCallDatasourceInfo();
            info3.setLabel("?????????");
            info3.setDataSource(Lists.newArrayList(edges.stream().map(LinkEdgeDTO::getMethod).distinct()
                .collect(Collectors.joining(","))));
            infos.add(info3);

            return infos;
        }
        if (NodeTypeGroupEnum.SEARCH.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();
            AppCallDatasourceInfo info1
                = new AppCallDatasourceInfo();
            info1.setLabel("????????????");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);
            return infos;
        }
        if (NodeTypeGroupEnum.OTHER.name().equals(node.getNodeTypeGroup())) {
            List<AppCallDatasourceInfo> infos = new ArrayList<>();
            AppCallDatasourceInfo info1
                = new AppCallDatasourceInfo();
            info1.setLabel("????????????");
            info1.setDataSource(Lists.newArrayList(node.getNodeName()));
            infos.add(info1);
            return infos;
        }
        throw new RuntimeException("????????????????????????:" + node.getNodeTypeGroup());
    }

    private void setNodeDefaultResponse(AbstractTopologyNodeResponse nodeResponse,
        LinkNodeDTO node,
        Map<String, LinkNodeDTO> nodeMap,
        Map<String, List<LinkEdgeDTO>> providerEdgeMap,
        Map<String, List<LinkEdgeDTO>> callEdgeMap,
        AtomicLong nextNumber) {
        // ?????????????????????????????????
        if (!isUnknownNode(node) && !isOuterService(node)) {
            nodeResponse.setNodeType(
                NodeTypeResponseEnum.getTypeByAmdbType(node.getNodeTypeGroup()));
        }
        // ?????????????????????
        if (isUnknownNode(node)) {
            nodeResponse.setNodeType(NodeTypeResponseEnum.UNKNOWN);
        }
        // ???????????????
        if (isOuterService(node)) {
            nodeResponse.setNodeType(NodeTypeResponseEnum.OUTER);
        }
        // ????????????
        if (isVirtualNode(node)) {
            nodeResponse.setNodeType(NodeTypeResponseEnum.VIRTUAL);
        }
        nodeResponse.setLabel(getNodeInfoLabel(node, nextNumber));
        nodeResponse.setRoot(isRoot(node));
        nodeResponse.setId(node.getNodeId());
        nodeResponse.setUpAppNames(getUpAppNames(node, nodeMap, providerEdgeMap));
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private List<NodeDetailDatasourceInfo> getNodeDetails(LinkNodeDTO node,
        Map<String, LinkNodeDTO> nodeMap,
        Map<String, List<LinkEdgeDTO>> providerEdgeMap, Map<String, List<LinkEdgeDTO>> callEdgeMap,
        Map<String, List<ApplicationNodeDTO>> appNodeMap) {
        // ??????????????????????????????
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
     * ????????????????????????
     *
     * @param nodes ????????????
     * @return ???????????????
     */
    private List<ExceptionListResponse> getExceptionsFromNodes(List<AbstractTopologyNodeResponse> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }

        // ??????????????????
        List<ExceptionListResponse> unknownExceptions = nodes.stream().filter(this::isUnknownResponseNode)
            .map(node -> {
                ExceptionListResponse exception = new ExceptionListResponse();
                StringBuilder exceptionTitle = new StringBuilder();
                exceptionTitle.append("??????\"")
                    .append(StringUtils.join(node.getUpAppNames(), ","))
                    .append("\"?????????????????????????????????");
                if (CollectionUtils.isNotEmpty(node.getNodes())) {
                    exceptionTitle.append("?????????IP:")
                        .append(StringUtils.join(node.getNodes().stream()
                            .map(NodeDetailDatasourceInfo::getNode)
                            .collect(Collectors.toList()), ","));
                }

                exception.setTitle(exceptionTitle.toString());
                exception.setType("?????????????????????");
                exception.setSuggest("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                return exception;
            }).collect(Collectors.toList());

        // ????????????????????????
        List<ExceptionListResponse> middlewareExceptions = this.getMiddlewareExceptionList(nodes);
        unknownExceptions.addAll(middlewareExceptions);
        return unknownExceptions;
    }

    /**
     * ??????????????????????????????????????????
     * <p>
     * ????????????, ????????????????????? ??????id
     * ????????????id ?????????????????????????????????
     * ????????????, ???, ??????, ?????????
     *
     * @param nodes ??????
     * @return ???????????????
     */
    private List<ExceptionListResponse> getMiddlewareExceptionList(List<AbstractTopologyNodeResponse> nodes) {
        // ??????????????? ????????????
        List<String> applicationNameList = nodes.stream().flatMap(node -> node.getUpAppNames().stream())
            .distinct().collect(Collectors.toList());
        if (applicationNameList.isEmpty()) {
            return Collections.emptyList();
        }

        // ???????????????????????????????????????????????????????????????id
        List<Long> applicationIds = applicationDAO.listIdsByNameListAndCustomerId(applicationNameList);
        if (applicationIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ?????????????????? ??????????????????????????????
        Map<String, Map<Integer, Integer>> applicationNameAboutStatusCountMap = applicationMiddlewareService
            .getApplicationNameAboutStatusCountMap(applicationIds);
        if (applicationNameAboutStatusCountMap.isEmpty()) {
            return Collections.emptyList();
        }

        // ????????????
        int total = applicationNameList.size();
        int size = 500;
        int page = total / size + 1;

        // ??? 0 ??????, ??????????????? page, ????????????
        // ?????? ??????????????? ????????????
        // skip ??? 0 ??????, 0, 1; 2, 3; 4, 5; xxx
        return Stream.iterate(0, f -> f + 1).limit(page).parallel().flatMap(currentPage ->
            applicationNameList.stream().skip(currentPage * size).limit(size)
                .map(applicationName -> {
                    Map<Integer, Integer> statusAboutCount =
                        applicationNameAboutStatusCountMap.get(applicationName);
                    if (statusAboutCount == null) {
                        return null;
                    }

                    ExceptionListResponse exception = new ExceptionListResponse();
                    exception.setTitle(String.format("??????: %s ???????????????????????????", applicationName));
                    exception.setType("????????????????????????");

                    StringBuilder sb = new StringBuilder();
                    sb.append("??????????????? ");
                    if (statusAboutCount.get(ApplicationMiddlewareStatusEnum.NONE.getCode()) != null
                        && statusAboutCount.get(ApplicationMiddlewareStatusEnum.NONE.getCode()) > 0) {
                        sb.append("????????? ");
                    }

                    if (statusAboutCount.get(ApplicationMiddlewareStatusEnum.UNKNOWN.getCode()) != null
                        && statusAboutCount.get(ApplicationMiddlewareStatusEnum.UNKNOWN.getCode()) > 0) {
                        sb.append("?????? ");
                    }

                    if (statusAboutCount.get(ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode()) != null
                        && statusAboutCount.get(ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode()) > 0) {
                        sb.append("????????? ");
                    }

                    sb.append("????????????, ???????????????");
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

                // ???????????????
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

        // ???????????? Source --> Target ??????????????????
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
            return "???????????????" + edge.getMethod() + "??????????????????" + edge.getService();
        }
        //if (EdgeTypeGroupEnum.DUBBO.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_RPC + "").equals(edge.getRpcType())) {
            return "??????" + edge.getService() + "?????????(????????????" + edge.getMethod();
        }
        //if (EdgeTypeGroupEnum.DB.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_DB + "").equals(edge.getRpcType())) {
            return "????????????" + edge.getService() + ", ??????: " + edge.getMethod();
        }
        //if (EdgeTypeGroupEnum.MQ.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_MQ + "").equals(edge.getRpcType())) {
            // ?????????
            if (nodeMap.get(edge.getSourceId()).getNodeTypeGroup().equals(NodeTypeGroupEnum.MQ.getType())) {
                return "Topic???" + edge.getService() + "???Group???" + edge.getMethod();
            } else {
                return "Topic???" + edge.getService();
            }
        }
        //if (EdgeTypeGroupEnum.OSS.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_FS + "").equals(edge.getRpcType())) {
            return "URL???" + edge.getService();
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
                msg.add("DB???" + db);
            }
            if (StringUtils.isNotBlank(op)) {
                msg.add("???????????????" + op);
            }
            return StringUtils.join(msg, "???");
        }
        //if (EdgeTypeGroupEnum.JOB.name().equals(edge.getEagleTypeGroup())) {
        if ((MiddlewareType.TYPE_JOB + "").equals(edge.getRpcType())) {
            return "??????" + edge.getService() + "????????????" + edge.getMethod();
        } else if ((MiddlewareType.TYPE_SEARCH + "").equals(edge.getRpcType())) {
            return "Index???" + edge.getService() + "??????????????????" + edge.getMethod();
        }
        return "";
    }

    private boolean isRoot(LinkNodeDTO node) {
        return node.isRoot() && node.getNodeName().endsWith("-Virtual");
    }

    private String getNodeInfoLabel(LinkNodeDTO node, AtomicLong nextNumber) {
        String label = node.getNodeName();
        if (isRoot(node)) {
            label = "??????";
        } else if (node.getNodeTypeGroup().equalsIgnoreCase(NodeTypeGroupEnum.APP.getType())
            || node.getNodeTypeGroup().equalsIgnoreCase(NodeTypeGroupEnum.CACHE.getType())) {
            label = node.getNodeName();
        }
        // ????????????
        if (isUnknownNode(node)) {
            label = "????????????" + nextNumber.incrementAndGet();
        }
        // ???????????????
        else if (isOuterService(node)) {
            label = "????????????" + nextNumber.incrementAndGet();
        }
        return label;
    }

    private boolean nodeIsApp(LinkNodeDTO node) {
        return node.getNodeType().equalsIgnoreCase(NodeTypeEnum.APP.name());
    }

    /**
     * ?????????????????????
     * ????????????????????????
     *
     * @param node ????????????
     * @return ?????????
     */
    private boolean isUnknownNode(LinkNodeDTO node) {
        return "UNKNOWN".equalsIgnoreCase(node.getNodeName());
    }

    private boolean isUnknownResponseNode(AbstractTopologyNodeResponse node) {
        return node.getNodeType().getType().equals(NodeTypeResponseEnum.UNKNOWN.getType());
    }

    /**
     * ????????????
     * ????????????????????????
     *
     * @param node ????????????
     * @return ?????????
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
