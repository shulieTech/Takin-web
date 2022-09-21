package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.amdb.common.dto.link.topology.AppShadowDatabaseDTO;
import io.shulie.amdb.common.dto.link.topology.LinkEdgeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkNodeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.amdb.common.enums.EdgeTypeGroupEnum;
import io.shulie.amdb.common.enums.NodeTypeEnum;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationRemoteCallQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceDetailInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceInput;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceService;
import io.shulie.takin.web.biz.service.pressureresource.common.*;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.*;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.dao.pressureresource.*;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeChildEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.*;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDetailQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
@Service
public class PressureResourceCommonServiceImpl implements PressureResourceCommonService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceCommonServiceImpl.class);

    @Resource
    private PressureResourceService pressureResourceService;

    @Resource
    private PressureResourceRelateTableDAO pressureResourceRelateTableDAO;

    @Resource
    private PressureResourceRelateRemoteCallDAO pressureResourceRelateRemoteCallDAO;

    @Resource
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;

    @Resource
    private PressureResourceDetailDAO pressureResourceDetailDAO;

    @Resource
    private PressureResourceDAO pressureResourceDAO;

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    @Resource
    private PressureResourceRelateAppDAO pressureResourceRelateAppDAO;

    @Resource
    private SceneService sceneService;

    @Resource
    private ActivityDAO activityDao;

    @Resource
    private ActivityService activityService;

    @Resource
    private ApplicationEntranceClient applicationEntranceClient;

    @Resource
    private ApplicationClient applicationClient;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private DictionaryDataDAO dictionaryDataDAO;

    @Resource
    private InterfaceTypeChildDAO interfaceTypeChildDAO;

    @Resource
    private AppRemoteCallService appRemoteCallService;

    @Resource
    private AppRemoteCallDAO appRemoteCallDAO;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Resource
    private ApplicationDsDbManageDAO dsDbManageDAO;

    @Resource
    private ApplicationDsDAO applicationDsDAO;

    @Autowired
    private ApplicationDsCacheManageDAO dsCacheManageDAO;

    private static String TAKIN_RESOURCE_MODIFY_KEY = "TAKIN:RESOURCE:MODIFY:KEY";

    /**
     * 自动处理压测资源准备任务
     */
    @Override
    public void processAutoPressureResource() {
        // 租户下的所有业务流程
        BusinessFlowPageQueryRequest queryRequest = new BusinessFlowPageQueryRequest();
        queryRequest.setCurrentPage(0);
        queryRequest.setPageSize(1000);
        PagingList<BusinessFlowListResponse> flowList = sceneService.getBusinessFlowList(queryRequest);
        if (flowList == null || flowList.isEmpty() || CollectionUtils.isEmpty(flowList.getList())) {
            logger.warn("当前租户下业务流程为空,暂不处理压测资源准备!!!");
            return;
        }
        List<BusinessFlowListResponse> responseList = flowList.getList();
        responseList.stream().forEach(flow -> {
            // 业务流程Id
            Long flowId = flow.getId();
            // 业务流程名称
            String sceneName = flow.getSceneName();
            PressureResourceQueryParam queryParam = new PressureResourceQueryParam();
            queryParam.setSourceId(flowId);
            PagingList<PressureResourceEntity> pageList = pressureResourceDAO.pageList(queryParam);

            PressureResourceInput pressureResourceInput = new PressureResourceInput();
            pressureResourceInput.setName(sceneName);
            pressureResourceInput.setType(SourceTypeEnum.AUTO.getCode());
            pressureResourceInput.setCheckStatus(CheckStatusEnum.CHECK_NO.getCode());
            pressureResourceInput.setSourceId(flowId);
            // 设置归属人
            pressureResourceInput.setUserId(flow.getUserId());
            boolean insertFlag = true;
            if (!pageList.isEmpty() && !CollectionUtils.isEmpty(pageList.getList())) {
                // 修改
                PressureResourceEntity tmpEntity = pageList.getList().get(0);
                // 设置Id
                pressureResourceInput.setId(tmpEntity.getId());
                pressureResourceInput.setUserId(tmpEntity.getUserId());
                insertFlag = false;
            }
            // 处理详情
            List<PressureResourceDetailInput> detailInputs = Lists.newArrayList();

            // 获取业务流程关联业务活动
            List<SceneLinkRelateResult> relateResults = sceneService.getSceneLinkRelates(flowId);
            if (CollectionUtils.isNotEmpty(relateResults)) {
                List<Long> businessLinkIds = relateResults.stream().map(relate -> Long.valueOf(relate.getBusinessLinkId())).distinct().collect(Collectors.toList());
                ActivityQueryParam activityQueryParam = new ActivityQueryParam();
                activityQueryParam.setActivityIds(businessLinkIds);
                // 查询业务活动
                List<ActivityListResult> activityListResults = activityDao.getActivityList(activityQueryParam);
                if (CollectionUtils.isNotEmpty(activityListResults)) {
                    for (int i = 0; i < activityListResults.size(); i++) {
                        ActivityListResult activityListResult = activityListResults.get(i);
                        // 查询业务活动详情
                        ActivityInfoQueryRequest request = new ActivityInfoQueryRequest();
                        request.setActivityId(activityListResult.getActivityId());
                        ActivityResponse responseDetail = activityService.getActivityById(request);

                        // 找到已经匹配的业务活动
                        if (responseDetail.getBusinessType() != BusinessTypeEnum.VIRTUAL_BUSINESS.getType()) {
                            PressureResourceDetailInput detailInput = new PressureResourceDetailInput();
                            detailInput.setAppName(responseDetail.getApplicationName());
                            detailInput.setEntranceUrl(responseDetail.getServiceName());
                            detailInput.setEntranceName(responseDetail.getActivityName());
                            detailInput.setRpcType(responseDetail.getRpcType());
                            detailInput.setMethod(responseDetail.getMethod());
                            detailInput.setExtend(responseDetail.getExtend());
                            detailInput.setLinkId(responseDetail.getLinkId());
                            // 添加到集合中
                            detailInputs.add(detailInput);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(detailInputs)) {
                    pressureResourceInput.setDetailInputs(detailInputs);
                }
            }

            if (insertFlag) {
                // 新增
                pressureResourceService.add(pressureResourceInput);
            } else {
                pressureResourceService.update(pressureResourceInput);
            }
        });
    }

    /**
     * 自动梳理关联信息
     */
    @Override
    public void processAutoPressureResourceRelate(Long resourceId) {
        PressureResourceDetailQueryParam detailQueryParam = new PressureResourceDetailQueryParam();
        detailQueryParam.setResourceId(resourceId);
        List<PressureResourceDetailEntity> detailEntityList = pressureResourceDetailDAO.getList(detailQueryParam);
        if (CollectionUtils.isNotEmpty(detailEntityList)) {
            try {
                // 根据详情来处理
                for (int i = 0; i < detailEntityList.size(); i++) {
                    // 获取入口
                    PressureResourceDetailEntity detailEntity = detailEntityList.get(i);
                    Pair<List<PressureResourceRelateDsEntity>, List<PressureResourceRelateTableEntity>> pair = processDsAndTable(detailEntity);
                    // 远程调用梳理
                    List<PressureResourceRelateRemoteCallEntity> remoteCallEntityList = processRemoteCall(detailEntity);
                    pressureResourceRelateDsDAO.saveOrUpdate(pair.getLeft());
                    pressureResourceRelateTableDAO.saveOrUpdate(pair.getRight());
                    pressureResourceRelateRemoteCallDAO.saveOrUpdate(remoteCallEntityList);
                }
            } catch (Throwable e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void pushRedis(Long... resoureIds) {
        redisTemplate.opsForSet().add(TAKIN_RESOURCE_MODIFY_KEY, resoureIds);
    }

    @Override
    public List<Long> getResourceIdsFormRedis() {
        List list = Lists.newArrayList();
        try {
            list = redisTemplate.opsForSet().pop(TAKIN_RESOURCE_MODIFY_KEY, 20);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.EMPTY_LIST;
            }
        } catch (Throwable e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return list;
    }

    /**
     * 通过链路拓扑图来处理关联应用,关联数据源,关联表
     *
     * @param detailEntity
     * @return
     */
    private Pair<List<PressureResourceRelateDsEntity>, List<PressureResourceRelateTableEntity>>
    processDsAndTable(PressureResourceDetailEntity detailEntity) {
        // 需要新增的数据源列表
        List<PressureResourceRelateDsEntity> dsEntityList = Lists.newArrayList();
        // 需要新增的表信息
        List<PressureResourceRelateTableEntity> tableEntityList = Lists.newArrayList();
        Long resourceId = detailEntity.getResourceId();
        // 链路拓扑图查询
        ApplicationEntranceTopologyQueryRequest request = new ApplicationEntranceTopologyQueryRequest();
        request.setApplicationName(detailEntity.getAppName());
        request.setLinkId(detailEntity.getLinkId());
        request.setMethod(detailEntity.getMethod());
        request.setRpcType(detailEntity.getRpcType());
        request.setExtend(detailEntity.getExtend());
        request.setServiceName(detailEntity.getEntranceUrl());
        request.setType(EntranceTypeEnum.getEnumByType(detailEntity.getRpcType()));
        // 大数据查询拓扑图
        LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
                false, request.getApplicationName(), request.getLinkId(), request.getServiceName(), request.getMethod(),
                request.getRpcType(), request.getExtend(), true);
        // 关联表和数据源处理
        if (applicationEntrancesTopology != null) {
            // 获取应用节点
            List<LinkNodeDTO> nodeDTOList = applicationEntrancesTopology.getNodes();
            List<LinkNodeDTO> appNodeList = nodeDTOList.stream()
                    .filter(node -> {
                        if (node.getNodeType().equals(NodeTypeEnum.APP.getType()) && !"UNKNOWN".equals(node.getNodeName())) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
            List<PressureResourceRelateAppEntity> appEntityList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(appNodeList)) {
                appEntityList = appNodeList.stream().map(appNode -> {
                    PressureResourceRelateAppEntity appEntity = new PressureResourceRelateAppEntity();
                    appEntity.setAppName(appNode.getNodeName());
                    appEntity.setResourceId(resourceId);
                    appEntity.setDetailId(detailEntity.getId());
                    appEntity.setTenantId(WebPluginUtils.traceTenantId());
                    appEntity.setEnvCode(WebPluginUtils.traceEnvCode());
                    // 节点数默认为0
                    appEntity.setNodeNum(0);
                    // 默认不正常
                    appEntity.setStatus(1);
                    // 通过应用去查询状态
                    List<ApplicationListResponse> list = applicationService.getApplicationList(appEntity.getAppName());
                    if (CollectionUtils.isNotEmpty(list)) {
                        Response<ApplicationVo> voResponse = applicationService.getApplicationInfo(String.valueOf(list.get(0).getApplicationId()));
                        if (voResponse.getSuccess()) {
                            ApplicationVo applicationVo = voResponse.getData();
                            // 默认等于探针在线节点数
                            appEntity.setNodeNum(applicationVo.getOnlineNodeNum());
                            appEntity.setStatus(0);
                        }
                    }
                    appEntity.setJoinPressure(JoinFlagEnum.YES.getCode());
                    appEntity.setType(SourceTypeEnum.AUTO.getCode());
                    return appEntity;
                }).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(appEntityList)) {
                // 保存关联应用
                pressureResourceRelateAppDAO.saveOrUpdate(appEntityList);
            }
            List<LinkEdgeDTO> edgeDTOList = applicationEntrancesTopology.getEdges();
            // 获取所有的数据库操作信息
            List<LinkEdgeDTO> dbEdgeList = edgeDTOList.stream().filter(edge -> {
                if (edge.getEagleTypeGroup().equals(EdgeTypeGroupEnum.DB.getType())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dbEdgeList)) {
                // 按照URL分组
                Map<String, List<LinkEdgeDTO>> serviceMap = dbEdgeList.stream().collect(Collectors.groupingBy(dbEdge -> fetchKey(dbEdge)));

                for (Map.Entry<String, List<LinkEdgeDTO>> entry : serviceMap.entrySet()) {
                    String key = entry.getKey();
                    PressureResourceRelateDsEntity dsEntity = new PressureResourceRelateDsEntity();
                    dsEntity.setResourceId(resourceId);
                    dsEntity.setDetailId(detailEntity.getId());
                    dsEntity.setAppName(key.split("#")[0]);
                    if ("null".equals(dsEntity.getAppName())) {
                        // TODO 暂时打印下日志
                        logger.error("关联数据源名称为空 key,{} value,{}", entry.getValue(), JSON.toJSONString(entry.getValue()));
                        continue;
                    }
                    String database = key.split("#")[1];
                    String dbName = DbNameUtil.getDbName(database);
                    if (PtUtils.isShadow(dbName)) {
                        continue;
                    }
                    // 从任意的边里面获取数据源详情信息
                    LinkEdgeDTO edgeDTO = entry.getValue().get(0);
                    List<AppShadowDatabaseDTO> dsList = edgeDTO.getDsList();
                    if (CollectionUtils.isEmpty(dsList)) {
                        logger.warn("应用数据源未梳理完成,{}", database);
                    } else {
                        AppShadowDatabaseDTO appShadowDatabaseDTO = dsList.get(0);
                        dsEntity.setBusinessUserName(appShadowDatabaseDTO.getTableUser());
                        dsEntity.setMiddlewareName(appShadowDatabaseDTO.getConnectionPool());
                        dsEntity.setMiddlewareType(appShadowDatabaseDTO.getMiddlewareType());
                    }
                    dsEntity.setBusinessDatabase(database);
                    dsEntity.setTenantId(WebPluginUtils.traceTenantId());
                    dsEntity.setEnvCode(WebPluginUtils.traceEnvCode());
                    dsEntity.setStatus(StatusEnum.NO.getCode());
                    dsEntity.setType(SourceTypeEnum.AUTO.getCode());
                    dsEntity.setGmtCreate(new Date());
                    // 生成唯一key,关联表
                    String uniqueKey = DataSourceUtil.generateDsKey(resourceId, database);
                    dsEntity.setUniqueKey(uniqueKey);

                    dsEntityList.add(dsEntity);

                    List<LinkEdgeDTO> value = entry.getValue();
                    if (CollectionUtils.isNotEmpty(value)) {
                        for (int k = 0; k < value.size(); k++) {
                            String method = value.get(k).getMethod();
                            // 过滤掉影子的表
                            if (PtUtils.isShadow(method)) {
                                continue;
                            }
                            PressureResourceRelateTableEntity tableEntity = new PressureResourceRelateTableEntity();
                            tableEntity.setResourceId(resourceId);
                            if (StringUtils.isBlank(method)) {
                                logger.warn("链路梳理结果错误,表信息未梳理 {}", resourceId);
                                continue;
                            }
                            tableEntity.setBusinessTable(method);
                            tableEntity.setDsKey(uniqueKey);
                            tableEntity.setGmtCreate(new Date());

                            tableEntity.setJoinFlag(JoinFlagEnum.YES.getCode());
                            tableEntity.setStatus(StatusEnum.NO.getCode());
                            tableEntity.setType(SourceTypeEnum.AUTO.getCode());
                            tableEntity.setTenantId(WebPluginUtils.traceTenantId());
                            tableEntity.setEnvCode(WebPluginUtils.traceEnvCode());

                            tableEntityList.add(tableEntity);
                        }
                    }
                }
            }
        }
        return Pair.of(dsEntityList, tableEntityList);
    }

    /**
     * 处理关联的远程调用信息
     *
     * @param detailEntity
     * @return
     */
    private List<PressureResourceRelateRemoteCallEntity> processRemoteCall(PressureResourceDetailEntity detailEntity) {
        // 通过linkId去查询远程调用
        ApplicationRemoteCallQueryDTO callQueryDTO = new ApplicationRemoteCallQueryDTO();
        callQueryDTO.setLinkId(detailEntity.getLinkId());
        callQueryDTO.setQueryTye("2");
        PagingList<ApplicationRemoteCallDTO> pageList = applicationClient.listApplicationRemoteCalls(callQueryDTO);
        if (pageList.isEmpty()) {
            return Collections.emptyList();
        }
        // 数据字段增加后，也在枚举中增加下
        List<TDictionaryVo> voList = dictionaryDataDAO.getDictByCode("REMOTE_CALL_TYPE");
        Map<String, InterfaceTypeChildEntity> childEntityMap = interfaceTypeChildDAO.selectToMapWithNameKey();
        List<ApplicationRemoteCallDTO> list = pageList.getList();
        // 保存
        List<PressureResourceRelateRemoteCallEntity> callEntityList = list.stream().map(item -> {
            PressureResourceRelateRemoteCallEntity callEntity = new PressureResourceRelateRemoteCallEntity();
            callEntity.setResourceId(detailEntity.getResourceId());
            callEntity.setDetailId(detailEntity.getId());
            callEntity.setAppName(item.getAppName());
            callEntity.setStatus(StatusEnum.NO.getCode());
            callEntity.setPass(1);
            callEntity.setRpcId(item.getRpcId());
            callEntity.setInterfaceName(RemoteCallUtils.getInterfaceNameByRpcName(item.getMiddlewareName(), item.getServiceName(), item.getMethodName()));
            callEntity.setInterfaceType(appRemoteCallService.getInterfaceType(item.getMiddlewareName(), voList));
            if (!childEntityMap.containsKey(item.getMiddlewareDetail())) {
                callEntity.setInterfaceChildType(item.getMiddlewareName());
            } else {
                callEntity.setInterfaceChildType(item.getMiddlewareDetail());
            }
            callEntity.setMd5(RemoteCallUtils.buildRemoteCallName(callEntity.getAppName(), callEntity.getInterfaceName(), callEntity.getInterfaceType()));
            callEntity.setType(AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType());
            callEntity.setManualTag(0);
            callEntity.setTenantId(WebPluginUtils.traceTenantId());
            callEntity.setEnvCode(WebPluginUtils.traceEnvCode());

            // 通过MD5去查询本地远程调用数据
            AppRemoteCallResult appRemoteCallResult = appRemoteCallDAO.queryOne(callEntity.getAppName(), callEntity.getInterfaceType(), callEntity.getInterfaceName());
            if (appRemoteCallResult != null) {
                callEntity.setServerAppName(appRemoteCallResult.getServerAppName());
                callEntity.setType(appRemoteCallResult.getType());
                callEntity.setIsSynchronize(appRemoteCallResult.getIsSynchronize() ? 0 : 1);
                // 是否放行 - 调用方和非调用方为非http类型的，默认自动放行，开关：开；其余的为关
                // 0 http 2 feign 1 double
                if (callEntity.getInterfaceType().intValue() != 0 || callEntity.getInterfaceType() != 2) {
                    callEntity.setPass(PassEnum.PASS_YES.getCode());
                }
            }
            // 重新设置下远程调用类型,兼容下原有的类型
            callEntity.setType(RemoteCallUtil.getType(callEntity));
            return callEntity;
        }).collect(Collectors.toList());
        return callEntityList;
    }

    // 应用+数据源
    private String fetchKey(LinkEdgeDTO dbEdge) {
        return dbEdge.getServerAppName() + "#" + dbEdge.getService();
    }

    /**
     * 同步修改数据源配置到应用上
     *
     * @param resouceId
     */
    @Override
    public void syncDs(Long resouceId) {
        // 读取resourceId
        PressureResourceEntity resourceEntity = pressureResourceMapper.selectById(resouceId);
        // 判断隔离方式
        if (resourceEntity == null) {
            return;
        }
        int isolateType = resourceEntity.getIsolateType().intValue();
        if (isolateType == IsolateTypeEnum.DEFAULT.getCode()) {
            logger.warn("{}格式方式未设置,暂不同步", resourceEntity.getName());
            return;
        }
        // 影子库
        if (isolateType == IsolateTypeEnum.SHADOW_DB_TABLE.getCode()) {
            processShadowDatabase(resourceEntity);
        }
    }

    /**
     * 同步影子库
     *
     * @param resourceEntity
     */
    private void processShadowDatabase(PressureResourceEntity resourceEntity) {
        // 1、查询影子库信息
        //Long resourceId = resourceEntity.getId();
        //PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        //dsQueryParam.setResourceId(resourceId);
        //List<PressureResourceRelateDsEntity> dsList = pressureResourceRelateDsDAO.queryByParam(dsQueryParam);
        //// 处理应用关联的数据源
        //if (CollectionUtils.isEmpty(dsList)) {
        //    return;
        //}
        //// 按应用分组
        //Map<String, List<PressureResourceRelateDsEntity>> dsAppNameMap = dsList.stream()
        //        .filter(ds -> StringUtils.isNotBlank(ds.getAppName()))
        //        .collect(Collectors.groupingBy(PressureResourceRelateDsEntity::getAppName));
//
        //List<AppShadowDatabaseDTO> appShadowDatabaseDTOList = Lists.newArrayList();
        //for (Map.Entry<String, List<PressureResourceRelateDsEntity>> entry : dsAppNameMap.entrySet()) {
        //    // 根据应用名查询应用Id
        //    ApplicationDetailResult applicationDetailResult = applicationService.queryTApplicationMntByName(entry.getKey());
        //    List<PressureResourceRelateDsEntity> tmpDsList = entry.getValue();
        //    for (int i = 0; i < tmpDsList.size(); i++) {
        //        PressureResourceRelateDsEntity dsEntity = dsList.get(i);
        //        AppShadowDatabaseDTO appShadowDatabaseDTO = new AppShadowDatabaseDTO();
        //        appShadowDatabaseDTO.setAppName(dsEntity.getAppName());
        //        appShadowDatabaseDTO.setConnectionPool(dsEntity.getMiddlewareName());
        //        appShadowDatabaseDTO.setMiddlewareType(dsEntity.getMiddlewareType());
        //        appShadowDatabaseDTO.setDataSource(dsEntity.getBusinessDatabase());
        //        appShadowDatabaseDTO.setTableUser(dsEntity.getBusinessUserName());
        //        appShadowDatabaseDTO.setShadowDataSource(dsEntity.getShadowDatabase());
        //        appShadowDatabaseDTO.setType("_2");
        //        appShadowDatabaseDTOList.add(appShadowDatabaseDTO);
        //    }
//
        //    ApplicationDsQueryParam queryParam = new ApplicationDsQueryParam();
        //    queryParam.setApplicationId(applicationDetailResult.getApplicationId());
        //    queryParam.setSourceId(resourceId);
        //    queryParam.setIsDeleted(0);
        //    WebPluginUtils.fillQueryParam(queryParam);
        //    //filterAndSave(appShadowDatabaseDTOList, applicationDetailResult.getApplicationId(), queryParam);
        //}
    }
}
