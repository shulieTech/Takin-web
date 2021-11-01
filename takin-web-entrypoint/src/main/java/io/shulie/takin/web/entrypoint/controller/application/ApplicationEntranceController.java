package io.shulie.takin.web.entrypoint.controller.application;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.biz.service.LinkTopologyService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntrancesQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntrancesResponse;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.vo.WebOptionEntity;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@RestController
@RequestMapping("/api/application/entrances")
@Api(tags = "应用服务管理", value = "应用服务管理")
@Slf4j
public class ApplicationEntranceController {

    @Autowired
    private ApplicationEntranceClient applicationEntranceClient;

    @Autowired
    private LinkTopologyService linkTopologyService;

    @Autowired
    private ActivityDAO activityDAO;
    //
    //@Autowired
    //private ApplicationClient applicationClient;

    @GetMapping("/types")
    @ApiOperation("获得入口服务类型")
    public List<WebOptionEntity> getEntranceTypes() {
        EntranceTypeEnum[] values = EntranceTypeEnum.values();
        return Stream.of(values)
            .filter(item -> item != EntranceTypeEnum.RABBITMQ)
            .map(item -> {
                WebOptionEntity webOptionEntity = new WebOptionEntity();
                webOptionEntity.setLabel(item.name());
                webOptionEntity.setValue(item.name());
                return webOptionEntity;
            }).collect(Collectors.toList());
    }

    @GetMapping()
    @ApiOperation("获得入口服务列表")
    public List<ApplicationEntrancesResponse> getApplicationEntrances(@Validated
        ApplicationEntrancesQueryRequest request) {
        List<ServiceInfoDTO> applicationEntrances = applicationEntranceClient.getApplicationEntrances(
            request.getApplicationName(), request.getType().getType());
        if (CollectionUtils.isEmpty(applicationEntrances)) {
            return Lists.newArrayList();
        }
        return applicationEntrances.stream()
            .filter(item -> !item.getServiceName().startsWith("PT_"))
            .map(item -> {
                ApplicationEntrancesResponse applicationEntrancesResponse = new ApplicationEntrancesResponse();
                applicationEntrancesResponse.setMethod(item.getMethodName());
                applicationEntrancesResponse.setRpcType(item.getRpcType());
                applicationEntrancesResponse.setExtend(item.getExtend());
                applicationEntrancesResponse.setServiceName(item.getServiceName());
                applicationEntrancesResponse.setLabel(
                    ActivityUtil.serviceNameLabel(item.getServiceName(), item.getMethodName()));
                applicationEntrancesResponse.setValue(
                    ActivityUtil.createLinkId(item.getServiceName(), item.getMethodName(),
                        item.getAppName(), item.getRpcType(), item.getExtend()));
                return applicationEntrancesResponse;
                // 增加去重
            }).distinct().collect(Collectors.toList());
    }
    //
    //@ApiOperation("获得入口经过的应用上面的中间件信息")
    //@GetMapping("/middlewares")
    //public List getApplicationEntranceMiddlewares() {
    //    return null;
    //}

    @ApiOperation("获得入口服务拓扑图")
    @GetMapping("topology")
    public ApplicationEntranceTopologyResponse getApplicationEntrancesTopology(@Validated
        ApplicationEntranceTopologyQueryRequest request) {
        if (StringUtils.isEmpty(request.getApplicationName())) {
            throw new TakinWebException(ExceptionCode.APP_LINK_TOPOLOGY_ERROR, "没有应用名");
        }
        return linkTopologyService.getApplicationEntrancesTopology(request);
    }

    @PostMapping("/updateUnknownNode")
    @ApiOperation("标记未知节点")
    public Boolean updateUnknownNodeToOuter(@Validated @RequestBody ApplicationEntranceTopologyQueryRequest request) {
        return applicationEntranceClient.updateUnknownNodeToOuter(request.getApplicationName(), request.getLinkId(),
            request.getServiceName(), request.getMethod(),
            request.getRpcType(), request.getExtend(), request.getNodeId());
    }

    @GetMapping("/all")
    @ApiOperation("获取应用下所有入口服务列表")
    public List<ApplicationEntrancesResponse> getApplicationAllEntrances(String appName) {
        if(StringUtils.isBlank(appName)){
            log.error("应用名称不能为空");
            return Collections.emptyList();
        }
        List<ServiceInfoDTO> applicationEntrances = applicationEntranceClient.getApplicationEntrances(
                appName, "");
        if (CollectionUtils.isEmpty(applicationEntrances)) {
            return Lists.newArrayList();
        }
        //去重
        applicationEntrances = applicationEntrances.stream().collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(Comparator.comparing(ServiceInfoDTO::getServiceName))), ArrayList::new)
        );
        return applicationEntrances.stream()
                .filter(item -> !item.getServiceName().startsWith("PT_"))
                .map(item -> {
                    ApplicationEntrancesResponse applicationEntrancesResponse = new ApplicationEntrancesResponse();
                    applicationEntrancesResponse.setMethod(item.getMethodName());
                    applicationEntrancesResponse.setRpcType(item.getRpcType());
                    applicationEntrancesResponse.setExtend(item.getExtend());
                    applicationEntrancesResponse.setServiceName(item.getServiceName());
                    applicationEntrancesResponse.setLabel(
                            ActivityUtil.serviceNameLabel(item.getServiceName(), item.getMethodName()));
                    applicationEntrancesResponse.setValue(
                            ActivityUtil.createLinkId(item.getServiceName(), item.getMethodName(),
                                    item.getAppName(), item.getRpcType(), item.getExtend()));
                    return applicationEntrancesResponse;
                }).collect(Collectors.toList());
    }

    @GetMapping("/allByActivity")
    @ApiOperation("获取应用下创建业务活动所有入口服务列表")
    public List<ApplicationEntrancesResponse> getApplicationAllEntrancesByActivity(String appName) {
        if(StringUtils.isBlank(appName)){
            log.error("应用名称不能为空");
            return Collections.emptyList();
        }
        List<BusinessLinkManageTableEntity> activities = activityDAO.findActivityAppName(appName, appName + "%");
        if (CollectionUtils.isEmpty(activities)) {
            return Lists.newArrayList();
        }
        return activities.stream()
                .filter(item -> {
                        String entrace = item.getEntrace();
                        String[] entraceArray = entrace.split("\\|");
                        return 4  == entraceArray.length;
                }).map(item -> {
                    String entrace = item.getEntrace();
                    String[] entraceArray = entrace.split("\\|");
                    ApplicationEntrancesResponse applicationEntrancesResponse = new ApplicationEntrancesResponse();
                    HashMap nameAndId = new HashMap();
                    nameAndId.put("linkId",item.getLinkId());
                    nameAndId.put("linkName",item.getLinkName());
                    applicationEntrancesResponse.setActivityNameAndId(nameAndId);
                    applicationEntrancesResponse.setLabel(
                            ActivityUtil.serviceNameLabel(entraceArray[2], entraceArray[1]));
                    applicationEntrancesResponse.setValue(
                            ActivityUtil.createLinkId(entraceArray[2], entraceArray[1],
                                    appName, entraceArray[3], ""));
                    return applicationEntrancesResponse;
                }).collect(Collectors.toList());
    }

    //@ApiOperation("获得入口服务拓扑图")
    //@GetMapping("topology1")
    //public ApplicationEntranceTopologyResponse1 getApplicationEntrancesTopology1(@Validated
    //    ApplicationEntranceTopologyQueryRequest request) {
    //    LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
    //        request.getApplicationName(), request.getLinkId(), request.getServiceName(), request.getMethod(),
    //        request.getRpcType(), request.getExtend());
    //    ApplicationEntranceTopologyResponse1 applicationEntranceTopologyResponse
    //        = new ApplicationEntranceTopologyResponse1();
    //    if (applicationEntrancesTopology != null) {
    //        applicationEntranceTopologyResponse.setNodes(convertNodes(applicationEntrancesTopology.getNodes()));
    //        applicationEntranceTopologyResponse.setEdges(convertEdges(applicationEntrancesTopology.getEdges(), request));
    //    } else {
    //        applicationEntranceTopologyResponse.setNodes(Lists.newArrayList());
    //        applicationEntranceTopologyResponse.setEdges(Lists.newArrayList());
    //    }
    //    return applicationEntranceTopologyResponse;
    //}
    //
    //@GetMapping("detail")
    //@ApiOperation("获得入口服务链路详情")
    //public List<ApplicationEntranceDetailResponse1> getApplicationEntrancesDetail(@Validated
    //    ApplicationEntranceDetailQueryRequest request) {
    //    LinkTopologyDTO topology = applicationEntranceClient.getApplicationEntrancesTopology(
    //        request.getApplicationName(), request.getLinkId(), request.getServiceName(), request.getMethod(),
    //        request.getRpcType(), request.getExtend());
    //
    //    if (topology == null) {
    //        return Lists.newArrayList();
    //    }
    //    List<LinkNodeDTO> nodes = topology.getNodes();
    //    if (CollectionUtils.isEmpty(nodes)) {
    //        return Lists.newArrayList();
    //    }
    //    Map<String, LinkNodeDTO> nodeMap = nodes.stream().collect(
    //        Collectors.toMap(LinkNodeDTO::getNodeId, self -> self));
    //    List<LinkEdgeDTO> edges = topology.getEdges();
    //    Map<String, List<LinkEdgeDTO>> sourceEdges = edges.stream().collect(
    //        Collectors.groupingBy(LinkEdgeDTO::getSourceId));
    //    Map<String, List<LinkEdgeDTO>> targetEdges = edges.stream().collect(
    //        Collectors.groupingBy(LinkEdgeDTO::getTargetId));
    //
    //    LinkNodeDTO root = nodes.stream().filter(LinkNodeDTO::isRoot).findFirst().orElse(null);
    //    if (root == null) {
    //        throw new RuntimeException("缺少Root节点");
    //    }
    //
    //    // 找到所有应用节点
    //    List<ApplicationEntranceDetailResponse1> appNodes = nodes.stream()
    //        .filter(node -> NodeTypeEnum.APP.name().equals(node.getNodeType()))
    //        .filter(node -> !isRoot(node))
    //        .filter(node -> !isUnknownNode(node))
    //        .map(node -> {
    //
    //            // 基础信息
    //            ApplicationEntranceDetailResponse1 applicationEntranceDetailResponse
    //                = new ApplicationEntranceDetailResponse1();
    //            applicationEntranceDetailResponse.setApplicationName(node.getNodeName());
    //
    //            // 查询调用的服务
    //            applicationEntranceDetailResponse.setCallServices(convertCallService(node, targetEdges, sourceEdges,
    //                nodeMap));
    //
    //            // 查询提供的服务
    //            applicationEntranceDetailResponse.setProviderServices(
    //                convertProviderService(node, targetEdges, sourceEdges, nodeMap, request));
    //
    //            // 查询下游服务
    //            applicationEntranceDetailResponse.setDownStreams(
    //                convertDownstreamService(node, targetEdges, sourceEdges, nodeMap));
    //
    //            // 当前节点如果和root节点名字类似，就认为是入口应用，排序为第一个
    //            applicationEntranceDetailResponse.setRootApplication(root.getNodeName().startsWith(node.getNodeName()));
    //
    //            return applicationEntranceDetailResponse;
    //        })
    //        .sorted((o1, o2) -> {
    //            if (o1.getRootApplication() && o2.getRootApplication()) {
    //                return 0;
    //            }
    //            if (o1.getRootApplication()) {
    //                return -1;
    //            }
    //            return 1;
    //        })
    //        .collect(Collectors.toList());
    //
    //    // 查询实例信息
    //    convertInstanceService(appNodes, nodes);
    //
    //    return appNodes;
    //}
    //
    //// ===================================== private methods under line ===================================== //
    //
    //private List<ApplicationEntranceDetailProvidersResponse1> convertProviderService(LinkNodeDTO node,
    //    Map<String, List<LinkEdgeDTO>> targetEdges, Map<String, List<LinkEdgeDTO>> sourceEdges,
    //    Map<String, LinkNodeDTO> nodeMap, ApplicationEntranceDetailQueryRequest request) {
    //    // 提供服务的边
    //    List<LinkEdgeDTO> providerEdges = targetEdges.get(node.getNodeId());
    //    if (CollectionUtils.isEmpty(providerEdges)) {
    //        return Lists.newArrayList();
    //    }
    //    Map<EdgeTypeEnum, ApplicationEntranceDetailProvidersResponse1> resultMap = new HashMap<>();
    //    for (LinkEdgeDTO providerEdge : providerEdges) {
    //        EdgeTypeEnum edgeType = EdgeTypeEnum.getEdgeTypeEnum(providerEdge.getMiddlewareName());
    //        if(edgeType.getType().equalsIgnoreCase(EdgeTypeEnum.VIRTUAL.name())){
    //            providerEdge.setEagleType(request.getType().getType());
    //            providerEdge.setEagleTypeGroup(EdgeTypeGroupEnum.getEdgeTypeEnum(request.getType().getType()).getType());
    //            providerEdge.setMethod(request.getMethod());
    //            providerEdge.setService(request.getServiceName());
    //            providerEdge.setMiddlewareName(request.getType().getType());
    //            edgeType = EdgeTypeEnum.getEdgeTypeEnum(providerEdge.getMiddlewareName());
    //        }
    //        ApplicationEntranceDetailProvidersResponse1 response = resultMap.get(edgeType);
    //        String extendInfo = convertEdgeExtendInfo(providerEdge);
    //        if (response == null) {
    //            response = new ApplicationEntranceDetailProvidersResponse1();
    //            response.setServiceType(edgeType.name());
    //            response.setExtendInfo(Lists.newArrayList(extendInfo));
    //            resultMap.put(edgeType, response);
    //        } else {
    //            response.getExtendInfo().add(extendInfo);
    //        }
    //    }
    //    return Lists.newArrayList(resultMap.values());
    //}
    //
    //private List<ApplicationEntranceDetailDownStreamResponse1> convertDownstreamService(LinkNodeDTO node,
    //    Map<String, List<LinkEdgeDTO>> targetEdges, Map<String, List<LinkEdgeDTO>> sourceEdges,
    //    Map<String, LinkNodeDTO> nodeMap) {
    //    // source是自己，发出去的边就是调用到的服务
    //    List<LinkEdgeDTO> callEdges = sourceEdges.get(node.getNodeId());
    //    if (CollectionUtils.isEmpty(callEdges)) {
    //        return Lists.newArrayList();
    //    }
    //    Set<ApplicationEntranceDetailDownStreamResponse1> downStreams = new HashSet<>();
    //    // mq需要记录，找到下游消费者
    //    List<String> mqInfo = new ArrayList<>();
    //    for (LinkEdgeDTO callEdge : callEdges) {
    //        LinkNodeDTO linkNodeDTO = nodeMap.get(callEdge.getTargetId());
    //        if (linkNodeDTO == null) {
    //            continue;
    //        }
    //        if (NodeTypeGroupEnum.MQ.name().equals(linkNodeDTO.getNodeTypeGroup())) {
    //            String method = callEdge.getMethod();
    //            String service = callEdge.getService();
    //            mqInfo.add(callEdge.getTargetId() + "#" + service + "#" + method);
    //            continue;
    //        }
    //        if (!NodeTypeGroupEnum.APP.name().equals(linkNodeDTO.getNodeType())) {
    //            continue;
    //        }
    //        ApplicationEntranceDetailDownStreamResponse1 downstream
    //            = new ApplicationEntranceDetailDownStreamResponse1();
    //        downstream.setApplicationName(linkNodeDTO.getNodeName());
    //        downStreams.add(downstream);
    //    }
    //    for (String mq : mqInfo) {
    //        String[] split = mq.split("#");
    //        List<LinkEdgeDTO> linkEdgeDTOS = sourceEdges.get(split[0]);
    //        if (CollectionUtils.isEmpty(linkEdgeDTOS)) {
    //            break;
    //        }
    //        // 如果是mq，找他下级的消费者，service相同就标记是同一个
    //        List<String> secondTargetIds = linkEdgeDTOS.stream()
    //            .filter(linkEdgeDTO -> linkEdgeDTO.getService().equals(split[1]))
    //            .map(LinkEdgeDTO::getTargetId)
    //            .collect(Collectors.toList());
    //        for (String secondTargetId : secondTargetIds) {
    //            LinkNodeDTO linkNodeDTO = nodeMap.get(secondTargetId);
    //            if (linkNodeDTO == null) {
    //                continue;
    //            }
    //            if (!linkNodeDTO.getNodeType().equals(NodeTypeEnum.APP.name())) {
    //                continue;
    //            }
    //            ApplicationEntranceDetailDownStreamResponse1 downstream
    //                = new ApplicationEntranceDetailDownStreamResponse1();
    //            downstream.setApplicationName(linkNodeDTO.getNodeName());
    //            downStreams.add(downstream);
    //        }
    //    }
    //    return Lists.newArrayList(downStreams);
    //}
    //
    //private void convertInstanceService(List<ApplicationEntranceDetailResponse1> appNodes, List<LinkNodeDTO> nodes) {
    //    if (CollectionUtils.isEmpty(nodes) || CollectionUtils.isEmpty(appNodes)) {
    //        appNodes.forEach(appNode -> appNode.setInstances(Lists.newArrayList()));
    //        return;
    //    }
    //    List<String> appNames = nodes.stream().map(LinkNodeDTO::getNodeName).collect(Collectors.toList());
    //    ApplicationNodeQueryDTO applicationNodeQueryDTO = new ApplicationNodeQueryDTO();
    //    applicationNodeQueryDTO.setAppNames(StringUtils.join(appNames, ","));
    //    applicationNodeQueryDTO.setPageSize(99999);
    //    PagingList<ApplicationNodeDTO> applicationNodeDTOPagingList = applicationClient.pageApplicationNodes(
    //        applicationNodeQueryDTO);
    //    if (CollectionUtils.isEmpty(applicationNodeDTOPagingList.getList())) {
    //        appNodes.forEach(appNode -> appNode.setInstances(Lists.newArrayList()));
    //        return;
    //    }
    //    Map<String, List<ApplicationNodeDTO>> instanceMapByAppName = applicationNodeDTOPagingList.getList().stream()
    //        .collect(
    //            Collectors.groupingBy(ApplicationNodeDTO::getAppName));
    //    for (ApplicationEntranceDetailResponse1 appNode : appNodes) {
    //        appNode.setInstances(convertInstanceService(instanceMapByAppName.get(appNode.getApplicationName())));
    //    }
    //}
    //
    //private List<ApplicationEntranceDetailInstanceResponse1> convertInstanceService(
    //    List<ApplicationNodeDTO> applicationNodeDTOS) {
    //    if (CollectionUtils.isEmpty(applicationNodeDTOS)) {
    //        return Lists.newArrayList();
    //    }
    //    return applicationNodeDTOS.stream().map(node -> {
    //        ApplicationEntranceDetailInstanceResponse1 instance = new ApplicationEntranceDetailInstanceResponse1();
    //        instance.setIp(node.getIpAddress());
    //        instance.setAgentId(node.getAgentId());
    //        instance.setPid(node.getProgressId());
    //        return instance;
    //    }).collect(Collectors.toList());
    //}
    //
    //private List<ApplicationEntranceDetailCallResponse1> convertCallService(LinkNodeDTO node,
    //    Map<String, List<LinkEdgeDTO>> targetEdges,
    //    Map<String, List<LinkEdgeDTO>> sourceEdges,
    //    Map<String, LinkNodeDTO> nodeMap) {
    //    // source是自己，发出去的边就是调用到的服务
    //    List<LinkEdgeDTO> callEdges = sourceEdges.get(node.getNodeId());
    //    if (CollectionUtils.isEmpty(callEdges)) {
    //        return Lists.newArrayList();
    //    }
    //    Map<EdgeTypeEnum, List<LinkEdgeDTO>> collect = callEdges.stream().collect(
    //        Collectors.groupingBy(edge -> EdgeTypeEnum.getEdgeTypeEnum(edge.getMiddlewareName())));
    //
    //    List<ApplicationEntranceDetailCallResponse1> calls = new ArrayList<>();
    //    // key:调用类型，value调用表
    //    collect.forEach((key, value) -> {
    //        ApplicationEntranceDetailCallResponse1 call = new ApplicationEntranceDetailCallResponse1();
    //        call.setServiceType(key.name());
    //
    //        List<ApplicationEntranceDetailCallItemResponse1> items = new ArrayList<>();
    //        // key：调用的目标NodeId，value：调用的边
    //        if (CollectionUtils.isNotEmpty(value)) {
    //            Map<String, List<LinkEdgeDTO>> collect1 = value.stream().collect(
    //                Collectors.groupingBy(LinkEdgeDTO::getTargetId));
    //
    //            collect1.forEach((k, v) -> {
    //                LinkNodeDTO linkNodeDTO = nodeMap.get(k);
    //                ApplicationEntranceDetailCallItemResponse1 callItem
    //                    = new ApplicationEntranceDetailCallItemResponse1();
    //                callItem.setTitle(convertNodeTitle(linkNodeDTO));
    //                callItem.setInfo(convertEdgeCallInfo(v));
    //                items.add(callItem);
    //            });
    //        }
    //        if (CollectionUtils.isNotEmpty(items)) {
    //            call.setItems(items);
    //        }
    //        calls.add(call);
    //    });
    //
    //    return calls;
    //}
    //
    //private List<String> convertEdgeCallInfo(List<LinkEdgeDTO> v) {
    //    if (CollectionUtils.isEmpty(v)) {
    //        return Lists.newArrayList();
    //    }
    //    return v.stream().map(this::convertEdgeExtendInfo).collect(Collectors.toList());
    //}
    //
    //private String convertNodeTitle(LinkNodeDTO node) {
    //    if (node.getExtendInfo() == null) {
    //        return "-";
    //    }
    //    if (NodeTypeGroupEnum.MQ.name().equals(node.getNodeTypeGroup())) {
    //        NodeExtendInfoForMQDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
    //            NodeExtendInfoForMQDTO.class);
    //        return "节点[" + extendInfo.getIp() + ":" + extendInfo.getPort() + "]";
    //    }
    //    if (NodeTypeGroupEnum.CACHE.name().equals(node.getNodeTypeGroup())) {
    //        NodeExtendInfoForCacheDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
    //            NodeExtendInfoForCacheDTO.class);
    //        return "节点[" + extendInfo.getIp() + ":" + extendInfo.getPort() + "]";
    //    }
    //    if (NodeTypeGroupEnum.DB.name().equals(node.getNodeTypeGroup())) {
    //        NodeExtendInfoForDBDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
    //            NodeExtendInfoForDBDTO.class);
    //        return "节点[" + extendInfo.getIp() + ":" + extendInfo.getPort() + "]";
    //    }
    //    if (NodeTypeGroupEnum.OSS.name().equals(node.getNodeTypeGroup())) {
    //        NodeExtendInfoForOSSDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
    //            NodeExtendInfoForOSSDTO.class);
    //        return "节点[" + extendInfo.getIp() + ":" + extendInfo.getPort() + "]";
    //    }
    //    if (NodeTypeGroupEnum.SEARCH.name().equals(node.getNodeTypeGroup())) {
    //        NodeExtendInfoForSearchDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
    //            NodeExtendInfoForSearchDTO.class);
    //        return "节点[" + extendInfo.getIp() + ":" + extendInfo.getPort() + "]";
    //    }
    //    if (NodeTypeGroupEnum.APP.name().equals(node.getNodeTypeGroup())) {
    //        NodeExtendInfoForSearchDTO extendInfo = convertNodeExtendInfo(node.getExtendInfo(),
    //            NodeExtendInfoForSearchDTO.class);
    //        return "节点[" + extendInfo.getIp() + ":" + extendInfo.getPort() + "]";
    //    }
    //    return "-";
    //}
    //
    //private <T> T convertNodeExtendInfo(Object extendInfo, Class<T> clazz) {
    //    String json = JSON.toJSONString(extendInfo);
    //    return JSON.parseObject(json, clazz);
    //}
    //
    //private String convertEdgeExtendInfo(LinkEdgeDTO edge) {
    //    if (EdgeTypeGroupEnum.HTTP.name().equals(edge.getEagleTypeGroup())) {
    //        return "请求方式：" + edge.getMethod() + "，请求路径：" + edge.getService();
    //    }
    //    if (EdgeTypeGroupEnum.DUBBO.name().equals(edge.getEagleTypeGroup())) {
    //        return "类：" + edge.getService() + "，方法(参数）：" + edge.getMethod();
    //    }
    //    if (EdgeTypeGroupEnum.DB.name().equals(edge.getEagleTypeGroup())) {
    //        return "数据库：" + edge.getService();
    //    }
    //    if (EdgeTypeGroupEnum.MQ.name().equals(edge.getEagleTypeGroup())) {
    //        return "Topic：" + edge.getService() + "，Group：" + edge.getMethod();
    //    }
    //    if (EdgeTypeGroupEnum.OSS.name().equals(edge.getEagleTypeGroup())) {
    //        return "URL：" + edge.getService();
    //    }
    //    if (EdgeTypeGroupEnum.CACHE.name().equals(edge.getEagleTypeGroup())) {
    //        String db = null;
    //        String op = null;
    //        if (edge.getService().contains(":")) {
    //            String[] split = edge.getService().split(":");
    //            db = split[0];
    //            op = split[1];
    //        } else {
    //            db = edge.getService();
    //            op = edge.getMethod();
    //        }
    //        List<String> msg = new ArrayList<>();
    //        if (StringUtils.isNotBlank(db)) {
    //            msg.add("DB：" + db);
    //        }
    //        if (StringUtils.isNotBlank(op)) {
    //            msg.add("操作方式：" + op);
    //        }
    //        return StringUtils.join(msg, "，");
    //    }
    //    if (EdgeTypeGroupEnum.JOB.name().equals(edge.getEagleTypeGroup())) {
    //        return "类：" + edge.getService()+ "，方法：" + edge.getMethod();
    //    }
    //    return "";
    //}
    //
    //private List<ApplicationEntranceTopologyEdgeResponse1> convertEdges(List<LinkEdgeDTO> edges,
    //    ApplicationEntranceTopologyQueryRequest request) {
    //    if (CollectionUtils.isEmpty(edges)) {
    //        return Lists.newArrayList();
    //    }
    //    // amdb没有对边进行聚合，这边进行聚合处理
    //    Map<String, List<LinkEdgeDTO>> edgeGroups = edges.stream()
    //        .map(edge->{
    //            if(edge.getEagleType().equals("VIRTUAL")){
    //                edge.setEagleType(request.getType().getType());
    //                edge.setEagleTypeGroup(EdgeTypeGroupEnum.getEdgeTypeEnum(request.getType().getType()).getType());
    //                edge.setMethod(request.getMethod());
    //                edge.setService(request.getServiceName());
    //                edge.setMiddlewareName(request.getType().getType());
    //            }
    //            return edge;
    //        })
    //        .collect(Collectors.groupingBy(item ->
    //            StringUtils.join(Lists
    //                    .newArrayList(item.getSourceId(), item.getTargetId(), item.getMiddlewareName(),
    //                        item.getRpcType()),
    //                "#")
    //        ));
    //    return edgeGroups.entrySet().stream()
    //        .map(item -> {
    //            String[] split = item.getKey().split("#");
    //            ApplicationEntranceTopologyEdgeResponse1 applicationEntranceTopologyEdgeResponse
    //                = new ApplicationEntranceTopologyEdgeResponse1();
    //            applicationEntranceTopologyEdgeResponse.setSource(split[0]); // sourceId
    //            applicationEntranceTopologyEdgeResponse.setTarget(split[1]); // targetId
    //            EdgeTypeEnum edgeTypeEnum = EdgeTypeEnum.getEdgeTypeEnum(split[2]);// middlewareName
    //            applicationEntranceTopologyEdgeResponse.setLabel(edgeTypeEnum.name());
    //            applicationEntranceTopologyEdgeResponse.setType(edgeTypeEnum.name());
    //            List<String> extInfos = item.getValue().stream()
    //                .map(this::convertEdgeExtendInfo)
    //                .distinct()
    //                .collect(Collectors.toList());
    //            if (CollectionUtils.isNotEmpty(extInfos)) {
    //                applicationEntranceTopologyEdgeResponse.setExtendInfo(extInfos);
    //            }
    //            return applicationEntranceTopologyEdgeResponse;
    //        }).collect(Collectors.toList());
    //}
    //
    //private List<ApplicationEntranceTopologyNodeResponse1> convertNodes(List<LinkNodeDTO> nodes) {
    //    if (CollectionUtils.isEmpty(nodes)) {
    //        return Lists.newArrayList();
    //    }
    //    return nodes.stream().map(item -> {
    //        ApplicationEntranceTopologyNodeResponse1 applicationEntranceTopologyNodeResponse
    //            = new ApplicationEntranceTopologyNodeResponse1();
    //        applicationEntranceTopologyNodeResponse.setLabel(isRoot(item) ? "入口" : item.getNodeName());
    //        applicationEntranceTopologyNodeResponse.setRoot(item.isRoot());
    //        applicationEntranceTopologyNodeResponse.setId(item.getNodeId());
    //        applicationEntranceTopologyNodeResponse.setType(NodeTypeEnum.getNodeType(item.getNodeType()));
    //        if (!isRoot(item) && item.getExtendInfo() != null) {
    //            applicationEntranceTopologyNodeResponse.setTitle(convertNodeTitle(item));
    //        }
    //        return applicationEntranceTopologyNodeResponse;
    //    }).collect(Collectors.toList());
    //}
    //
    //private boolean isRoot(LinkNodeDTO node) {
    //    return node.isRoot() && node.getNodeName().endsWith("-Virtual");
    //}
    //
    //private boolean isUnknownNode(LinkNodeDTO node) {
    //    return node.getNodeName().equalsIgnoreCase("UNKNOWN");// AMDB定义在拓扑图里面的
    //}
}
