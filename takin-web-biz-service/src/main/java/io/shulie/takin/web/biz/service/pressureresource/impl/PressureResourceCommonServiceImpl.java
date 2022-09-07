package io.shulie.takin.web.biz.service.pressureresource.impl;

import io.shulie.amdb.common.dto.link.topology.LinkEdgeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkNodeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceDetailInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceInput;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceService;
import io.shulie.takin.web.biz.service.pressureresource.common.DataSourceUtil;
import io.shulie.takin.web.biz.service.pressureresource.common.SourceTypeEnum;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.pressureresource.*;
import io.shulie.takin.web.data.model.mysql.pressureresource.*;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDetailQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;

    @Resource
    private PressureResourceDetailDAO pressureResourceDetailDAO;

    @Resource
    private PressureResourceDAO pressureResourceDAO;

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
            boolean insertFlag = true;
            if (pageList.isEmpty() || CollectionUtils.isEmpty(pageList.getList())) {
                // 新增压测准备配置
                pressureResourceInput.setName(sceneName);
                pressureResourceInput.setType(SourceTypeEnum.AUTO.getCode());
                pressureResourceInput.setSourceId(flowId);
            } else {
                // 修改
                PressureResourceEntity tmpEntity = pageList.getList().get(0);
                // 设置Id
                pressureResourceInput.setId(tmpEntity.getId());
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
                        ActivityInfoQueryRequest request = new ActivityInfoQueryRequest();
                        request.setActivityId(activityListResult.getActivityId());
                        ActivityResponse responseDetail = activityService.getActivityById(request);

                        // 保存详情,存在则更新
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
     * 自动梳理关联应用
     */
    @Override
    public void processAutoPressureResourceRelate(Long resourceId) {
        PressureResourceDetailQueryParam detailQueryParam = new PressureResourceDetailQueryParam();
        detailQueryParam.setResourceId(resourceId);
        List<PressureResourceDetailEntity> detailEntityList = pressureResourceDetailDAO.getList(detailQueryParam);
        if (CollectionUtils.isNotEmpty(detailEntityList)) {
            for (int i = 0; i < detailEntityList.size(); i++) {
                // 获取入口
                PressureResourceDetailEntity detailEntity = detailEntityList.get(i);
                // 链路拓扑图查询
                ApplicationEntranceTopologyQueryRequest request = new ApplicationEntranceTopologyQueryRequest();
                request.setApplicationName(detailEntity.getAppName());
                request.setLinkId(detailEntity.getLinkId());
                request.setMethod(detailEntity.getMethod());
                request.setRpcType(detailEntity.getRpcType());
                request.setExtend(detailEntity.getExtend());
                request.setServiceName(detailEntity.getEntranceUrl());
                request.setType(EntranceTypeEnum.getEnumByType(detailEntity.getRpcType()));
                // 拓扑图查询
                // 大数据查询拓扑图
                LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
                        false, request.getApplicationName(), request.getLinkId(), request.getServiceName(), request.getMethod(),
                        request.getRpcType(), request.getExtend());
                if (applicationEntrancesTopology == null) {
                    logger.warn("链路拓扑图未梳理完成,{}", detailEntity.getEntranceUrl());
                    continue;
                }
                // 获取应用节点
                List<LinkNodeDTO> nodeDTOList = applicationEntrancesTopology.getNodes();
                List<LinkNodeDTO> appNodeList = nodeDTOList.stream().filter(node -> node.getNodeType().equals(ApplicationEntranceTopologyResponse.NodeTypeResponseEnum.APP.getType())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(appNodeList)) {
                    List<PressureResourceRelateAppEntity> appEntityList = appNodeList.stream().map(appNode -> {
                        PressureResourceRelateAppEntity appEntity = new PressureResourceRelateAppEntity();
                        appEntity.setAppName(appNode.getNodeName());
                        appEntity.setResourceId(resourceId);
                        appEntity.setDetailId(detailEntity.getId());
                        appEntity.setType(SourceTypeEnum.AUTO.getCode());
                        appEntity.setTenantId(WebPluginUtils.traceTenantId());
                        appEntity.setEnvCode(WebPluginUtils.traceEnvCode());

                        return appEntity;
                    }).collect(Collectors.toList());
                    // 保存关联应用
                    pressureResourceRelateAppDAO.saveOrUpdate(appEntityList);
                }
                List<LinkEdgeDTO> edgeDTOList = applicationEntrancesTopology.getEdges();
                // 获取所有的数据库操作信息
                List<LinkEdgeDTO> dbEdgeList = edgeDTOList.stream().filter(edge -> {
                    if (edge.getRpcType().equals("4") && edge.getLogType().equals("2")) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(dbEdgeList)) {
                    // 按照URL分组
                    Map<String, List<LinkEdgeDTO>> serviceMap = dbEdgeList.stream().collect(Collectors.groupingBy(dbEdge -> fetchKey(dbEdge)));

                    List<PressureResourceRelateDsEntity> dsEntityList = Lists.newArrayList();
                    List<PressureResourceRelateTableEntity> tableEntityList = Lists.newArrayList();
                    for (Map.Entry<String, List<LinkEdgeDTO>> entry : serviceMap.entrySet()) {
                        String key = entry.getKey();
                        PressureResourceRelateDsEntity dsEntity = new PressureResourceRelateDsEntity();
                        dsEntity.setResourceId(resourceId);
                        dsEntity.setDetailId(detailEntity.getId());
                        dsEntity.setAppName(key.split("#")[0]);
                        dsEntity.setBusinessDatabase(key.split("#")[1]);
                        dsEntity.setType(SourceTypeEnum.AUTO.getCode());
                        dsEntity.setTenantId(WebPluginUtils.traceTenantId());
                        dsEntity.setEnvCode(WebPluginUtils.traceEnvCode());
                        String uniqueKey = DataSourceUtil.generateKey(dsEntity);
                        dsEntity.setUniqueKey(uniqueKey);

                        dsEntityList.add(dsEntity);

                        List<LinkEdgeDTO> value = entry.getValue();
                        if (CollectionUtils.isNotEmpty(value)) {
                            for (int k = 0; k < value.size(); k++) {
                                String method = value.get(i).getMethod();
                                PressureResourceRelateTableEntity tableEntity = new PressureResourceRelateTableEntity();
                                tableEntity.setResourceId(resourceId);
                                tableEntity.setBusinessTable(method);
                                tableEntity.setDsKey(uniqueKey);
                                tableEntity.setJoinFlag(1);
                                tableEntity.setGmtCreate(new Date());
                                tableEntity.setType(SourceTypeEnum.AUTO.getCode());
                                dsEntity.setTenantId(WebPluginUtils.traceTenantId());
                                dsEntity.setEnvCode(WebPluginUtils.traceEnvCode());

                                tableEntityList.add(tableEntity);
                            }
                        }
                    }
                    pressureResourceRelateDsDAO.saveOrUpdate(dsEntityList);
                    pressureResourceRelateTableDAO.saveOrUpdate(tableEntityList);
                }
            }
        }
    }

    private String fetchKey(LinkEdgeDTO dbEdge) {
        return dbEdge.getServerAppName() + "#" + dbEdge.getService();
    }
}
