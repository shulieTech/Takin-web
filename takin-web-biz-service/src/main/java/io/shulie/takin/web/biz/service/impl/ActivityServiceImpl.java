package io.shulie.takin.web.biz.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.TimeVO;
import io.shulie.amdb.common.dto.link.topology.LinkNodeDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.amdb.common.enums.NodeTypeEnum;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.adapter.api.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.ext.content.enums.RpcTypeEnum;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.api.NotifyClient;
import io.shulie.takin.web.amdb.util.EntranceTypeUtils;
import io.shulie.takin.web.biz.aspect.ActivityCacheAspect;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.constant.BusinessActivityRedisKeyConstant;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.convert.activity.ActivityServiceConvert;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityResultQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ListApplicationRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityBottleneckResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityVerifyResponse;
import io.shulie.takin.web.biz.pojo.response.activity.BusinessApplicationListResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.LinkTopologyService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.domain.ErrorInfo;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.mapper.mysql.LinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.model.mysql.LinkManageTableEntity;
import io.shulie.takin.web.data.param.activity.ActivityCreateParam;
import io.shulie.takin.web.data.param.activity.ActivityExistsQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityUpdateParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.activity.ActivityResult;
import io.shulie.takin.web.data.result.application.ApplicationListResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shiyajian
 * create: 2020-12-30
 */
@Slf4j
@Component
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private SceneLinkRelateDAO sceneLinkRelateDAO;

    @Autowired
    RedisClientUtil redisClientUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private NotifyClient notifyClient;
    @Autowired
    private ActivityDAO activityDAO;
    @Autowired
    private LinkTopologyService linkTopologyService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private CloudTaskApi cloudTaskApi;
    @Autowired
    private ScriptManageService scriptManageService;
    @Autowired
    private SceneManageService sceneManageService;
    @Autowired
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;
    @Autowired
    private LinkManageTableMapper linkManageTableMapper;
    @Autowired
    private ApplicationEntranceClient applicationEntranceClient;

    @Autowired
    private ApplicationBusinessActivityService applicationBusinessActivityService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public List<BusinessApplicationListResponse> listApplicationByBusinessActivityIds(List<Long> businessActivityIds,
                                                                                      String applicationName) {
        Set<String> applicationNames = this.listApplicationNameByActivityIds(businessActivityIds, applicationName);
        if (applicationNames.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据应用名称, 用户id, 获得应用列表
//        List<ApplicationListResult> applicationList = applicationDAO.listByApplicationNamesAndUserId(
//                applicationNames, WebPluginUtils.traceUserId());
        List<ApplicationListResult> applicationList = applicationDAO.listByApplicationNamesAndUserId(
                applicationNames, null);
        if (applicationList.isEmpty()) {
            return Collections.emptyList();
        }

        return applicationList.stream().map(application -> {
            BusinessApplicationListResponse response = new BusinessApplicationListResponse();
            response.setApplicationId(application.getApplicationId().toString());
            response.setApplicationName(application.getApplicationName());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public PagingList<BusinessApplicationListResponse> listApplicationByBusinessFlowIds(
            ListApplicationRequest listApplicationRequest) {
        // 根据业务流程ids获取业务活动ids
        List<Long> activityIds = sceneLinkRelateDAO.listBusinessLinkIdsByBusinessFlowIds(
                listApplicationRequest.getBusinessFlowIds());

        // 根据业务活动ids获取应用名称
        Set<String> applicationNames = this.listApplicationNameByActivityIds(activityIds, listApplicationRequest.getApplicationName());
        if (applicationNames.isEmpty()) {
            return PagingList.empty();
        }

        // 列表
        // 根据应用名称, 用户id, 获得应用列表
        PagingList<ApplicationListResult> applicationPage = applicationDAO.pageByApplicationNamesAndUserId(
                applicationNames, listApplicationRequest);
        if (applicationPage.getTotal() == 0) {
            return PagingList.empty();
        }

        // 转换
        return PagingList.of(applicationPage.getList().stream().map(application -> {
            BusinessApplicationListResponse response = new BusinessApplicationListResponse();
            response.setApplicationId(application.getApplicationId().toString());
            response.setApplicationName(application.getApplicationName());
            return response;
        }).collect(Collectors.toList()), applicationPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long createActivity(ActivityCreateRequest request) {
        // 检查业务活动是否能入库
        checkActivity(request);
        ActivityCreateParam createParam = new ActivityCreateParam();
        createParam.setActivityName(request.getActivityName());
        createParam.setEntranceName(request.getServiceName());
        createParam.setIsChange(false);
        createParam.setApplicationName(request.getApplicationName());
        Long applicationId = this.applicationService.queryApplicationIdByAppName(request.getApplicationName());
        if (null != applicationId) {
            createParam.setApplicationId(applicationId);
        }
        createParam.setType(request.getType());
        createParam.setActivityLevel(request.getActivityLevel());
        createParam.setIsCore(request.getIsCore());
        createParam.setBusinessDomain(request.getBusinessDomain());
        createParam.setRpcType(request.getRpcType());
        createParam.setMethod(request.getMethod());
        createParam.setServiceName(request.getServiceName());
        createParam.setExtend(request.getExtend());
        createParam.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
        if (RpcTypeEnum.HTTP.getValue().equals(request.getRpcType())) {
            request.setServiceName(JmxUtil.pathGuiYi(request.getServiceName()));
        }
        createParam.setEntrance(
                ActivityUtil.buildEntrance(request.getMethod(), request.getServiceName(), request.getRpcType()));
        activityDAO.createActivity(createParam);
        notifyClient.startApplicationEntrancesCalculate(request.getApplicationName(), request.getServiceName(),
                request.getMethod(), request.getRpcType(), request.getExtend());
        return createParam.getLinkId();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long createActivityWithoutAMDB(ActivityCreateRequest request) {
        // 检查业务活动是否能入库
        checkActivity(request);
        ActivityCreateParam createParam = new ActivityCreateParam();
        createParam.setActivityName(request.getActivityName());
        createParam.setEntranceName(request.getServiceName());
        createParam.setIsChange(false);
        createParam.setApplicationName(request.getApplicationName());
        createParam.setType(request.getType());
        createParam.setActivityLevel(request.getActivityLevel());
        createParam.setIsCore(request.getIsCore());
        createParam.setBusinessDomain(request.getBusinessDomain());
        createParam.setRpcType(request.getRpcType());
        createParam.setMethod(request.getMethod());
        createParam.setServiceName(request.getServiceName());
        createParam.setExtend(request.getExtend());
        createParam.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
        createParam.setEntrance(
                ActivityUtil.buildEntrance(request.getMethod(), request.getServiceName(), request.getRpcType()));
        createParam.setPersistence(request.isPersistence());
        return activityDAO.createActivity(createParam);
    }

    /**
     * 检查正常业务活动
     *
     * @param request 请求参数
     */
    private void checkActivity(ActivityCreateRequest request) {
        ActivityExistsQueryParam param = new ActivityExistsQueryParam();
        param.setActivityName(request.getActivityName());
        List<String> exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("保存失败，[名称:%s] 已被使用", request.getActivityName()));
        }

        param.setActivityName(null);
        param.setApplicationName(request.getApplicationName());
        param.setActivityType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
        param.setType(request.getType());
        param.setEntranceName(request.getServiceName());
        param.setExtend(request.getExtend());
        param.setMethod(request.getMethod());
        param.setRpcType(request.getRpcType());
        param.setServiceName(request.getServiceName());
        exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("保存失败，[应用名:%s,类型:%s,入口:%s]与业务活动【%s】一致",
                            request.getApplicationName(), request.getType().getType(), request.getServiceName(), exists.get(0)));
        }
    }

    @Override
    public Long createVirtualActivity(VirtualActivityCreateRequest request) {
        // 检查虚拟业务活动是否能入库
        checkVirtualActivity(request);
        ActivityCreateParam createParam = new ActivityCreateParam();
        createParam.setActivityName(request.getActivityName());
        createParam.setIsChange(false);
        WebPluginUtils.fillUserData(createParam);
        createParam.setActivityLevel(request.getActivityLevel());
        createParam.setIsCore(request.getIsCore());
        createParam.setBusinessDomain(request.getBusinessDomain());
        // rpcType 修改
        createParam.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
        createParam.setEntrance(
                ActivityUtil.buildVirtualEntrance(request.getMethodName(), request.getVirtualEntrance(),
                        EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType()));
        //单独字段存中间软件类型
        createParam.setServerMiddlewareType(request.getType());
        activityDAO.createActivityNew(createParam);
        return createParam.getLinkId();
    }

    private void checkVirtualActivity(VirtualActivityCreateRequest request) {
        ActivityExistsQueryParam param = new ActivityExistsQueryParam();
        param.setActivityName(request.getActivityName());
        List<String> exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("保存失败，[名称:%s] 已被使用", request.getActivityName()));
        }
        param = new ActivityExistsQueryParam();
        param.setActivityType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
        param.setVirtualEntrance(request.getVirtualEntrance());
        param.setMethod(request.getMethodName());
        // rpc转化
        param.setRpcType(EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType());
        exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("保存失败，[虚拟入口:%s]与虚拟业务【%s】", request.getVirtualEntrance(), exists.get(0)));
        }
    }

    @Override
    public void updateVirtualActivity(VirtualActivityUpdateRequest request) {
        // 校验业务活动更新参数
        ActivityResult oldActivity = checkVirtualActivityUpdate(request);

        ActivityUpdateParam updateParam = new ActivityUpdateParam();
        updateParam.setActivityId(request.getActivityId());
        updateParam.setActivityName(request.getActivityName());
        WebPluginUtils.fillUserData(updateParam);
        updateParam.setIsChange(false);
        updateParam.setChangeBefore(oldActivity.getChangeAfter());
        updateParam.setActivityLevel(request.getActivityLevel());
        updateParam.setBusinessDomain(request.getBusinessDomain());
        updateParam.setIsCore(request.getIsCore());
        updateParam.setServerMiddlewareType(request.getType());
        // rpcType
        updateParam.setEntrance(
                ActivityUtil.buildVirtualEntrance(request.getMethodName(), request.getVirtualEntrance(),
                        EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType()));
        activityDAO.updateActivityNew(updateParam);
    }

    /**
     * 校验虚拟入口更新参数
     *
     * @return -
     */
    private ActivityResult checkVirtualActivityUpdate(VirtualActivityUpdateRequest request) {
        ActivityResult oldActivity = getActivityResult(request.getActivityId(), request.getActivityName());
        ActivityExistsQueryParam param = new ActivityExistsQueryParam();
        param.setMethod(request.getMethodName());
        param.setVirtualEntrance(request.getVirtualEntrance());
        // rpc转化
        param.setRpcType(EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType());
        param.setActivityType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
        List<String> exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            Optional<String> any = exists.stream().filter(item -> !item.equals(request.getActivityName())).findAny();
            if (any.isPresent()) {
                throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                        String.format("保存失败，虚拟入口已[业务活动：%s：入口：%s]已被使用，对应的虚拟业务活动名为：%s", request.getActivityName(),
                                request.getVirtualEntrance(), any.get()));
            }
        }
        return oldActivity;
    }

    /**
     * check
     *
     * @param activityId   活动id
     * @param activityName 活动名称
     * @return 活动结果
     */
    private ActivityResult getActivityResult(Long activityId, String activityName) {
        ActivityResult oldActivity = activityDAO.getActivityById(activityId);
        if (oldActivity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("修改失败，ID:[%s]对应的数据不存在", activityId));
        }

        ActivityExistsQueryParam param = new ActivityExistsQueryParam();
        if (activityName != null) {
            param.setActivityName(activityName);
            List<String> exists = activityDAO.exists(param);
            if (CollectionUtils.isNotEmpty(exists)) {
                Optional<String> any = exists.stream().filter(item -> !item.equals(activityName)).findAny();
                if (any.isPresent()) {
                    throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                            String.format("保存失败，业务活动[%s]已被使用，对应的虚拟业务活动为：%s", activityName, any.get()));
                }

            }
        }
        return oldActivity;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateActivity(ActivityUpdateRequest request) {
        // 校验业务活动更新参数
        ActivityResult oldActivity = checkActivityUpdate(request);

        OperationLogContextHolder.addVars(BizOpConstants.Vars.ENTRY_APPLICATION, request.getApplicationName());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE, request.getServiceName());
        ActivityUpdateParam updateParam = new ActivityUpdateParam();
        updateParam.setActivityId(request.getActivityId());
        updateParam.setActivityName(request.getActivityName());
        updateParam.setApplicationName(request.getApplicationName());
        updateParam.setEntranceName(request.getServiceName());
        updateParam.setType(request.getType());
        WebPluginUtils.fillUserData(updateParam);
        updateParam.setIsChange(isChange(oldActivity, request));
        updateParam.setChangeBefore(oldActivity.getChangeAfter());
        updateParam.setActivityLevel(request.getActivityLevel());
        updateParam.setBusinessDomain(request.getBusinessDomain());
        updateParam.setIsCore(request.getIsCore());
        updateParam.setRpcType(request.getRpcType());
        updateParam.setMethod(request.getMethod());
        updateParam.setServiceName(request.getServiceName());
        updateParam.setExtend(request.getExtend());
        if (RpcTypeEnum.HTTP.getValue().equals(request.getRpcType())) {
            request.setServiceName(JmxUtil.pathGuiYi(request.getServiceName()));
        }
        updateParam.setEntrance(ActivityUtil.buildEntrance(request.getMethod(), request.getServiceName(), request.getRpcType()));
        // 技术链路id
        updateParam.setLinkId(oldActivity.getLinkId());
        activityDAO.updateActivity(updateParam);

        // 非核心字段变动，不需要重建链路
        if (StringUtil.equals(request.getApplicationName(), oldActivity.getApplicationName())
                && StringUtil.equals(request.getServiceName(), oldActivity.getServiceName())
                && StringUtil.equals(request.getMethod(), oldActivity.getMethod())
                && StringUtil.equals(request.getRpcType(), oldActivity.getRpcType())
                && StringUtil.equals(request.getExtend(), oldActivity.getExtend())) {
            return;
        }

        notifyClient.stopApplicationEntrancesCalculate(oldActivity.getApplicationName(), oldActivity.getServiceName(),
                oldActivity.getMethod(), oldActivity.getRpcType(), oldActivity.getExtend());
        notifyClient.startApplicationEntrancesCalculate(request.getApplicationName(), request.getServiceName(),
                request.getMethod(), request.getRpcType(), request.getExtend());
    }

    private ActivityResult checkActivityUpdate(ActivityUpdateRequest request) {
        ActivityResult oldActivity = getActivityResult(request.getActivityId(), request.getActivityName());

        if (request.getServiceName() != null && request.getRpcType() != null && request.getApplicationName() != null) {
            ActivityExistsQueryParam param = new ActivityExistsQueryParam();
            param.setEntranceName(request.getServiceName());
            param.setApplicationName(request.getApplicationName());
            param.setType(request.getType());
            param.setExtend(request.getExtend());
            param.setMethod(request.getMethod());
            param.setRpcType(request.getRpcType());
            param.setServiceName(request.getServiceName());
            param.setActivityType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
            List<String> exists = activityDAO.exists(param);
            if (CollectionUtils.isNotEmpty(exists)) {
                Optional<String> any = exists.stream()
                    .filter(item -> !item.equals(oldActivity.getActivityName()))
                    .findAny();
                if (any.isPresent()) {
                    throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR, String
                            .format("保存失败，入口已[应用名称：%s，类型：%s，入口：%s]已被使用，对应的虚拟业务活动为：%s", request.getActivityName(),
                                    request.getType().getType(), request.getServiceName(), any.get()));
                }
            }
        }
        return oldActivity;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteActivity(Long activityId) {
        ActivityResult oldActivity = activityDAO.getActivityById(activityId);
        if (oldActivity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("删除失败，ID:[%s]对应的数据不存在", activityId));

        }
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_ACTIVITY, oldActivity.getActivityName());
        OperationLogContextHolder.addVars(Vars.ENTRANCE_TYPE, oldActivity.getType().name());
        OperationLogContextHolder.addVars(Vars.ENTRANCE,
                StringUtils.isNotBlank(oldActivity.getEntranceName()) ? oldActivity.getEntranceName() : oldActivity.getVirtualEntrance());
        activityDAO.deleteActivity(activityId);
        //记录业务活动删除事件
        redisClientUtil.hmset(Vars.ACTIVITY_DELETE_EVENT,
                oldActivity.getTenantId() + ":" + oldActivity.getEnvCode() + ":" + activityId, 0);
        // 正常业务活动
        if (oldActivity.getApplicationName() != null && oldActivity.getBusinessType().equals(
                BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
            notifyClient.stopApplicationEntrancesCalculate(oldActivity.getApplicationName(),
                    oldActivity.getServiceName(),
                    oldActivity.getMethod(), oldActivity.getRpcType(), oldActivity.getExtend());
        }
    }

    @Override
    public PagingList<ActivityListResponse> pageActivities(ActivityQueryRequest request) {
        ActivityQueryParam param = new ActivityQueryParam();
        param.setActivityName(request.getActivityName());
        param.setDomain(request.getDomain());
        param.setIsChange(request.getIsChange());
        param.setLinkLevel(request.getLinkLevel());
        param.setCurrent(request.getCurrent());
        param.setPageSize(request.getPageSize());
        param.setType(request.getType());
        param.setDeptId(request.getDeptId());
        WebPluginUtils.fillQueryParam(param);

        PagingList<ActivityListResult> activityListResultPagingList = activityDAO.pageActivities(param);

        List<ActivityListResponse> responses = activityListResultPagingList.getList().stream()
                .map(result -> {
                    ActivityListResponse response = new ActivityListResponse();
                    // 业务活动类型
                    response.setBusinessType(result.getBusinessType());
                    response.setActivityId(result.getActivityId());
                    response.setActivityName(result.getActivityName());
                    response.setIsChange(result.getIsChange());
                    response.setIsCore(String.valueOf(result.getIsCore()));
                    response.setIsDeleted(result.getIsDeleted());
                    response.setUserId(result.getUserId());
                    response.setUserName(result.getUserName());
                    response.setCreateTime(result.getCreateTime());
                    response.setUpdateTime(result.getUpdateTime());
                    response.setCanDelete(result.getCanDelete());
                    response.setBusinessDomain(result.getBusinessDomain());
                    response.setActivityLevel(result.getActivityLevel());
                    response.setDeptId(result.getDeptId());
                    WebPluginUtils.fillQueryResponse(response);
                    return response;
                }).collect(Collectors.toList());

        return PagingList.of(responses, activityListResultPagingList.getTotal());
    }

    @Override
    public ActivityBottleneckResponse getBottleneckByActivityList(
            ApplicationVisualInfoResponse applicationVisualInfoResponse,
            LocalDateTime startDateTime, LocalDateTime endTime, Map<String, List<E2eExceptionConfigInfoExt>> bottleneckConfigMap) {
        // 查询 瓶颈阈值 配置
        //List<E2eExceptionConfigInfoExt> bottleneckConfig = Lists.newArrayList();
        //if (WebPluginUtils.checkUserPlugin() && E2ePluginUtils.checkE2ePlugin()) {
        //    bottleneckConfig = E2ePluginUtils.getExceptionConfig(WebPluginUtils.traceTenantId(),
        //        WebPluginUtils.traceEnvCode());
        //}

        ApplicationEntranceTopologyResponse.AppProvider provider
                = new ApplicationEntranceTopologyResponse.AppProvider();
        provider.setServiceAvgRt(applicationVisualInfoResponse.getResponseConsuming());
        provider.setServiceAllSuccessRate(applicationVisualInfoResponse.getSuccessRatio());
        provider.setOwnerApps(applicationVisualInfoResponse.getAppName());
        provider.setServiceName(applicationVisualInfoResponse.getServiceAndMethod());
        provider.setRpcType(String.valueOf(applicationVisualInfoResponse.getRpcType()));

        // 瓶颈类型 init
        int rateBottleneckType = -1; // 瓶颈类型(-1 没有瓶颈)
        provider.setAllSuccessRateBottleneckType(rateBottleneckType);
        provider.setAllTotalRtBottleneckType(rateBottleneckType);
        provider.setAllSqlTotalRtBottleneckType(rateBottleneckType);

        if (applicationVisualInfoResponse.getRequestCount() != 0) { // 如果不是初始值，再计算瓶颈
            linkTopologyService.computeBottleneck(startDateTime.minusHours(8), null, bottleneckConfigMap, provider);
        }

        ActivityBottleneckResponse activityBottleneckResponse = new ActivityBottleneckResponse();
        // -1 没有瓶颈
        int init = -1;
        activityBottleneckResponse.setAllTotalRtBottleneckType(init);
        activityBottleneckResponse.setAllSuccessRateBottleneckType(init);
        activityBottleneckResponse.setAllSqlTotalRtBottleneckType(init);

        // 有 Rt 瓶颈
        if ((provider.getAllTotalRtBottleneckType() != -1)) {
            activityBottleneckResponse.setAllTotalRtBottleneckType(provider.getAllTotalRtBottleneckType());
            activityBottleneckResponse.setRtBottleneckId(provider.getRtBottleneckId());
        }
        // 成功率 瓶颈
        if ((provider.getAllSuccessRateBottleneckType() != -1)) {
            activityBottleneckResponse.setAllSuccessRateBottleneckType(provider.getAllSuccessRateBottleneckType());
            activityBottleneckResponse.setSuccessRateBottleneckId(provider.getSuccessRateBottleneckId());
        }
        // 慢 sql 瓶颈
        if ((provider.getAllSqlTotalRtBottleneckType() != -1)) {
            activityBottleneckResponse.setAllSqlTotalRtBottleneckType(provider.getAllSqlTotalRtBottleneckType());
            activityBottleneckResponse.setRtSqlBottleneckId(provider.getRtSqlBottleneckId());
        }

        return activityBottleneckResponse;
    }

    @Override
    public ActivityResponse getActivityWithMetricsById(ActivityInfoQueryRequest request) {
        ActivityResponse activity = getActivityById(request);

        // 非正常业务活动时，直接返回
        if (!activity.getBusinessType().equals(
                BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
            return activity;
        }

        // 拓扑图为空（说明没有流量），直接返回
        if (CollectionUtils.isEmpty(activity.getTopology().getNodes())) {
            return activity;
        }

        // 正常业务活动时，才对 拓扑图处理

        // 如果 起始时间 或 结束时间 为空，默认 查询5分钟的数据
        if (null == request.getStartTime() || null == request.getEndTime()) {
            LocalDateTime now = LocalDateTime.now();
            request.setEndTime(now);
            request.setStartTime(now.minusMinutes(5));
        }

        LocalDateTime startTimeUseInInFluxDB = request.getStartTime().minusHours(8);
        LocalDateTime endTimeUseInInFluxDB = request.getEndTime().minusHours(8);
        // line : 总调用量 startTimeUseInInFluxDB, 最近5 min
        //            allTotalCountStartDateTimeUseInInFluxDB = endTimeUseInInFluxDB.minusDays(1);
        LocalDateTime allTotalCountStartDateTimeUseInInFluxDB = startTimeUseInInFluxDB;

        linkTopologyService.fillMetrics(
                request,
                activity.getTopology(),
                startTimeUseInInFluxDB, endTimeUseInInFluxDB,
                allTotalCountStartDateTimeUseInInFluxDB);

        return activity;
    }

    @Override
    public ActivityResponse getActivityWithMetricsByIdForReport(Long activityId,
                                                                LocalDateTime startDateTime,
                                                                LocalDateTime endDateTime) {

        ActivityInfoQueryRequest activityInfoQueryRequest = new ActivityInfoQueryRequest();
        activityInfoQueryRequest.setActivityId(activityId);
        ActivityResponse activity = getActivityById(activityInfoQueryRequest);

        if (startDateTime == null || endDateTime == null) {
            return activity;
        }

        ActivityInfoQueryRequest request = new ActivityInfoQueryRequest();
        request.setActivityId(activityId);
        request.setFlowTypeEnum(FlowTypeEnum.BLEND);

        linkTopologyService.fillMetrics(
                request,
                activity.getTopology(),
                startDateTime, endDateTime,
                //默认不区分流量类型，按照混合流量查询
                startDateTime);

        return activity;
    }

    @Override
    public ActivityResponse getActivityById(ActivityInfoQueryRequest activityInfoQueryRequest) {
        ActivityResult result = activityDAO.getActivityById(activityInfoQueryRequest.getActivityId());
        if (result == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    activityInfoQueryRequest.getActivityId() + "对应的业务活动不存在");
        }
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setActivityId(result.getActivityId());
        activityResponse.setActivityName(result.getActivityName());
        activityResponse.setType(result.getType());

        if (result.getBusinessType().equals(BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
            // 正常业务活动
            activityResponse.setApplicationName(result.getApplicationName());
            activityResponse.setEntranceName(
                    ActivityUtil.serviceNameLabel(result.getServiceName(), result.getMethod()));
            activityResponse.setExtend(result.getExtend());
            activityResponse.setMethod(result.getMethod());
            activityResponse.setServiceName(result.getServiceName());
            activityResponse.setBindType(result.getType());
            activityResponse.setLinkId(ActivityUtil.createLinkId(result.getServiceName(), result.getMethod(),
                    result.getApplicationName(), result.getRpcType(), result.getExtend()));
        } else if (result.getBusinessType().equals(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
            // 虚拟业务活动下

            ActivityResult bindActivityResult = null;
            // 绑定的业务活动
            if (result.getBindBusinessId() != null) {
                bindActivityResult = activityDAO.getActivityById(result.getBindBusinessId());
            }

            if (bindActivityResult != null) {
                activityResponse.setApplicationName(bindActivityResult.getApplicationName());
                activityResponse.setExtend(bindActivityResult.getExtend());
                activityResponse.setMethod(bindActivityResult.getMethod());
                activityResponse.setServiceName(bindActivityResult.getServiceName());
                activityResponse.setLinkId(
                        ActivityUtil.createLinkId(bindActivityResult.getServiceName(), bindActivityResult.getMethod(),
                                bindActivityResult.getApplicationName(), bindActivityResult.getRpcType(),
                                bindActivityResult.getExtend()));
                // 用于下方，调用大数据用
                activityResponse.setBindType(bindActivityResult.getType());
            }
            activityResponse.setVirtualEntrance(result.getVirtualEntrance());
        }

        activityResponse.setBusinessType(result.getBusinessType());
        activityResponse.setChangeBefore(result.getChangeBefore());
        activityResponse.setChangeAfter(result.getChangeAfter());
        activityResponse.setIsChange(result.getIsChange());
        activityResponse.setUserId(result.getUserId());
        Map<Long, UserExt> userExtMap = WebPluginUtils.getUserMapByIds(Collections.singletonList(result.getUserId()));
        activityResponse.setUserName(WebPluginUtils.getUserName(result.getUserId(), userExtMap));
        activityResponse.setRpcType(result.getRpcType());
        activityResponse.setActivityLevel(result.getActivityLevel());
        activityResponse.setIsCore(result.getIsCore() == null ? "" : result.getIsCore().toString());
        activityResponse.setBusinessDomain(result.getBusinessDomain());

        // 拓扑图查询
        if (activityResponse.getBindType() != null) {
            ApplicationEntranceTopologyQueryRequest request = new ApplicationEntranceTopologyQueryRequest();
            request.setApplicationName(activityResponse.getApplicationName());
            request.setLinkId(activityResponse.getLinkId());
            request.setMethod(activityResponse.getMethod());
            request.setRpcType(activityResponse.getRpcType());
            request.setExtend(activityResponse.getExtend());
            request.setServiceName(activityResponse.getServiceName());
            request.setType(activityResponse.getType());
            activityResponse.setEnableLinkFlowCheck(ConfigServerHelper.getBooleanValueByKey(
                    ConfigServerKeyEnum.TAKIN_LINK_FLOW_CHECK_ENABLE));

            // 拓扑图查询
            activityResponse.setTopology(linkTopologyService
                    .getApplicationEntrancesTopology(request, activityInfoQueryRequest.isTempActivity()));
        }

        Integer verifyStatus = this.getVerifyStatus(activityInfoQueryRequest.getActivityId()).getVerifyStatus();
        activityResponse.setVerifyStatus(verifyStatus);
        activityResponse.setVerifiedFlag(
                verifyStatus.equals(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFIED));
        return activityResponse;
    }

    @Override
    public ActivityResponse getActivityByIdWithoutTopology(Long id) {
        ActivityResult result = activityDAO.getActivityById(id);
        if (result == null) {
            log.warn("查询{}对应的业务活动不存在", id);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR, "查询" + id + "对应的业务活动不存在");
        }
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setActivityId(result.getActivityId());
        activityResponse.setActivityName(result.getActivityName());
        activityResponse.setApplicationName(result.getApplicationName());
        activityResponse.setEntranceName(ActivityUtil.serviceNameLabel(result.getServiceName(), result.getMethod()));
        activityResponse.setType(result.getType());
        activityResponse.setChangeBefore(result.getChangeBefore());
        activityResponse.setChangeAfter(result.getChangeAfter());
        activityResponse.setIsChange(result.getIsChange());
        activityResponse.setUserId(result.getUserId());
        Map<Long, UserExt> userExtMap = WebPluginUtils.getUserMapByIds(Collections.singletonList(result.getUserId()));
        activityResponse.setUserName(WebPluginUtils.getUserName(result.getUserId(), userExtMap));
        activityResponse.setExtend(result.getExtend());
        activityResponse.setMethod(result.getMethod());
        activityResponse.setRpcType(result.getRpcType());
        activityResponse.setServiceName(result.getServiceName());
        activityResponse.setActivityLevel(result.getActivityLevel());
        activityResponse.setIsCore(result.getIsCore() == null ? "" : result.getIsCore().toString());
        activityResponse.setBusinessDomain(result.getBusinessDomain());
        activityResponse.setLinkId(ActivityUtil.createLinkId(result.getServiceName(), result.getMethod(),
                result.getApplicationName(), result.getRpcType(), result.getExtend()));
        Integer verifyStatus = getVerifyStatus(id).getVerifyStatus();
        activityResponse.setVerifyStatus(verifyStatus);
        activityResponse.setVerifiedFlag(
                verifyStatus.equals(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFIED));
        return activityResponse;
    }

    @Override
    public ActivityVerifyResponse verifyActivity(ActivityVerifyRequest request) {
        Long activityId = request.getActivityId();
        Long scriptId = request.getScriptId();
        ActivityVerifyResponse response = new ActivityVerifyResponse();
        response.setActivityId(activityId);
        response.setScriptId(scriptId);
        //1.根据业务活动ID查询缓存
        String reportId = redisClientUtil.getString(
                BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY + request.getActivityId());
        if (!StringUtil.isBlank(reportId)) {
            Integer verifyStatus = getVerifyStatus(activityId).getVerifyStatus();
            if (!verifyStatus.equals(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFIED)) {
                response.setVerifyStatus(verifyStatus);
                response.setTaskStatus(true);
                response.setVerifiedFlag(false);
                return response;
            }
        }
        //获取业务活动详情
        ActivityResult activityResult = activityDAO.getActivityById(request.getActivityId());
        //获取脚本详情
        ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(
                scriptId);
        SceneManageWrapperVO vo = new SceneManageWrapperVO();
        vo.setScriptId(request.getScriptId());
        vo.setPressureTestSceneName(SceneManageConstant.getFlowDebugSceneName(scriptId));
        vo.setIpNum(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_DEFAULT_IP_NUM);
        vo.setPressureTestTime(new TimeVO(1L, "m"));
        vo.setStopCondition(new ArrayList<>());
        vo.setScriptType(scriptManageDeployDetail.getType());
        vo.setConfigType(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_DEFAULT_CONFIG_TYPE);
        SceneBusinessActivityRefVO sceneBusinessActivityRefVO = new SceneBusinessActivityRefVO();
        sceneBusinessActivityRefVO.setBusinessActivityId(activityId);
        sceneBusinessActivityRefVO.setBusinessActivityName(activityResult.getActivityName());
        sceneBusinessActivityRefVO.setScriptId(scriptId);
        vo.setBusinessActivityConfig(Collections.singletonList(sceneBusinessActivityRefVO));
        SceneManageWrapperReq req = new SceneManageWrapperReq();
        WebResponse webResponse = sceneManageService.buildSceneForFlowVerify(vo, req, null);
        if (!webResponse.getSuccess()) {
            ErrorInfo error = webResponse.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            log.error("buildSceneForFlowVerify 异常,错误信息={}", JSON.toJSONString(error));
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "构造场景数据出现异常！原因为" + errorMsg);
        }
        //2.发起流量
        TaskFlowDebugStartReq taskFlowDebugStartReq = new TaskFlowDebugStartReq();
        List<SceneBusinessActivityRefOpen> businessActivityConfig = ScriptManageUtil
                .buildCloudBusinessActivityConfigList(vo.getBusinessActivityConfig());
        taskFlowDebugStartReq.setBusinessActivityConfig(businessActivityConfig);
        taskFlowDebugStartReq.setScriptId(scriptId);
        taskFlowDebugStartReq.setScriptType(scriptManageDeployDetail.getType());
        taskFlowDebugStartReq.setUploadFile(req.getUploadFile());
        List<PluginConfigDetailResponse> pluginConfigDetailResponseList = scriptManageDeployDetail
                .getPluginConfigDetailResponseList();
        if (CollectionUtils.isNotEmpty(pluginConfigDetailResponseList)) {
            List<Long> pluginIds = pluginConfigDetailResponseList.stream().map(o -> Long.parseLong(o.getName()))
                    .collect(Collectors.toList());
            taskFlowDebugStartReq.setEnginePluginIds(pluginIds);
            taskFlowDebugStartReq.setEnginePlugins(pluginConfigDetailResponseList.stream().map(
                    detail -> new EnginePluginsRefOpen() {{
                        setPluginId(Long.parseLong(detail.getName()));
                        setVersion(detail.getVersion());
                    }}
            ).collect(Collectors.toList()));
        }
        taskFlowDebugStartReq.setFeatures(req.getFeatures());
        UserExt user = WebPluginUtils.traceUser();
        if (user != null) {
            taskFlowDebugStartReq.setUserId(user.getId());
            taskFlowDebugStartReq.setUserName(user.getName());
        }
        //设置租户
        taskFlowDebugStartReq.setEnvCode(WebPluginUtils.traceEnvCode());
        taskFlowDebugStartReq.setTenantId(WebPluginUtils.traceTenantId());
        taskFlowDebugStartReq.setMachineId(req.getMachineId());
        taskFlowDebugStartReq.setEngineType(req.getEngineType());
        log.info("流量验证参数：{}", taskFlowDebugStartReq);
        Long startResult = cloudTaskApi.startFlowDebugTask(taskFlowDebugStartReq);
        log.info("流量验证发起结果：{}", startResult.toString());
        response.setTaskStatus(true);
        response.setVerifiedFlag(false);
        response.setVerifyStatus(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFYING);
        //3.缓存任务ID并返回
        redisClientUtil.setString(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY + activityId,
                String.valueOf(startResult), BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY_EXPIRE,
                TimeUnit.SECONDS);
        response.setScriptId(scriptId);
        //加入任务队列
        this.pushTaskToRedis(startResult);

        return response;
    }

    private void pushTaskToRedis(Long reportId) {
        if (reportId != null) {
            //兜底，默认压测2分钟，120s
            Long minutes = 2L;
            //兜底时长
            final LocalDateTime dateTime = LocalDateTime.now().plusMinutes(minutes);
            //组装
            SceneTaskDto taskDto = new SceneTaskDto(reportId, ContextSourceEnum.JOB_FLOW_VERIFY, dateTime);
            //任务添加到redis队列
            final String reportKeyName = WebRedisKeyConstant.getTaskList();
            final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
            redisTemplate.opsForList().leftPush(reportKeyName, reportKey);
            redisTemplate.opsForValue().set(reportKey, JSON.toJSONString(taskDto));
        }
    }

    @Override
    public ActivityVerifyResponse getVerifyStatus(Long activityId) {
        ActivityVerifyResponse response = new ActivityVerifyResponse();
        response.setActivityId(activityId);
        response.setVerifyStatus(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_UNVERIFIED);

        //1.从缓存获取taskId
        String reportId = redisClientUtil.getString(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY + activityId);
        //2.根据taskId获取报告状态即任务状态
        if (!StringUtil.isBlank(reportId)) {
            ReportDetailOutput responseResult = reportService.getReportByReportId(Long.valueOf(reportId));
            Integer verifyStatus = responseResult.getTaskStatus();
            response.setVerifyStatus(verifyStatus);
            response.setVerifiedFlag(
                    verifyStatus.equals(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFIED));
        }
        return response;
    }

    /**
     * @param activityId  业务ID
     * @param ownerApps   应用名称
     * @param serviceName 服务名称
     * @param state       开关状态
     */
    @Override
    public void setActivityNodeState(long activityId, String ownerApps, String serviceName, boolean state) {
        activityDAO.setActivityNodeServiceState(activityId, ownerApps, serviceName, state);
        tryClearActivityCache(activityId);//SY:节点状态发生改变则清空缓存信息
    }

    private void tryClearActivityCache(long activityId) {
        try {
            String key = ActivityCacheAspect.REDIS_PREFIX_KEY + activityId + "::*";
            Set keys = redisTemplate.keys(key);
            keys.forEach(k -> redisTemplate.delete(k));
        } catch (Exception e) {
            //Ignore
        }
    }

    @Override
    public List<ActivityNodeState> getActivityNodeServiceState(long activityId) {
        return activityDAO.getActivityNodeServiceState(activityId);
    }

    @Override
    public List<ActivityListResponse> queryNormalActivities(ActivityResultQueryRequest request) {
        if (request == null) {
            return null;
        }
        ActivityQueryParam queryParam = new ActivityQueryParam();
        queryParam.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
        if (StringUtil.isNotEmpty(request.getEntrancePath())) {
            queryParam.setEntrance(request.getEntrancePath());
        }
        if (StringUtil.isNotEmpty(request.getApplicationName())) {
            queryParam.setApplicationName(request.getApplicationName());
        }

        List<ActivityListResult> activityList = activityDAO.getActivityList(queryParam);
        return ActivityServiceConvert.INSTANCE.ofActivityList(activityList);
    }

    @Override
    public BusinessLinkManageTableEntity getActivityByName(String activityName) {
        return activityDAO.getActivityByName(activityName);
    }

    @Override
    public BusinessLinkManageTableEntity getActivity(ActivityCreateRequest request) {
        String entrance = ActivityUtil.buildEntrance(request.getMethod(), request.getServiceName(), request.getRpcType());
        List<Map<String, String>> serviceList = activityDAO.findActivityByServiceName(request.getApplicationName(),
                entrance);
        if (CollectionUtils.isEmpty(serviceList)) {
            return null;
        }
        BusinessLinkManageTableEntity businessLinkManageTableEntity = new BusinessLinkManageTableEntity();
        Map activityMap = serviceList.get(0);
        businessLinkManageTableEntity.setLinkId(Long.parseLong(activityMap.get("linkId").toString()));
        businessLinkManageTableEntity.setLinkName(activityMap.get("linkName").toString());
        businessLinkManageTableEntity.setPersistence(Objects.equals(activityMap.get("persistence").toString(), "1"));
        return businessLinkManageTableEntity;
    }

    /**
     * 根据业务活动id,查询关联应用名
     *
     * @param activityId
     * @return
     */
    @Override
    public List<String> processAppNameByBusinessActiveId(Long activityId) {
        BusinessLinkManageTableEntity businessLinkManageTableEntity =
                businessLinkManageTableMapper.selectById(activityId);
        if (businessLinkManageTableEntity == null) {
            return Lists.newArrayList();
        }
        // 虚拟入口 返回数据
        if (!ActivityUtil.isNormalBusiness(businessLinkManageTableEntity.getType())) {
            return Lists.newArrayList();
        }

        LinkManageTableEntity linkManageTableEntity =
                linkManageTableMapper.selectById(businessLinkManageTableEntity.getRelatedTechLink());
        if (linkManageTableEntity == null) {
            return Lists.newArrayList();
        }

        String features = linkManageTableEntity.getFeatures();
        if (StrUtil.isBlank(features)) {
            return new ArrayList<>(0);
        }

        Map<String, String> map = JSON.parseObject(features, Map.class);
        if (CollectionUtil.isEmpty(map)) {
            return new ArrayList<>(0);
        }

        String serviceName = map.get(FeaturesConstants.SERVICE_NAME_KEY);
        String methodName = map.get(FeaturesConstants.METHOD_KEY);
        String rpcType = map.get(FeaturesConstants.RPC_TYPE_KEY);
        String extend = map.get(FeaturesConstants.EXTEND_KEY);
        LinkTopologyDTO applicationEntrancesTopology = applicationEntranceClient.getApplicationEntrancesTopology(
                false, linkManageTableEntity.getApplicationName(), null, serviceName, methodName,
                rpcType, extend,false);
        if (applicationEntrancesTopology == null || CollectionUtils.isEmpty(applicationEntrancesTopology.getNodes())) {
            return new ArrayList<>(Collections.singleton(linkManageTableEntity.getApplicationName()));
        }
        return applicationEntrancesTopology.getNodes().stream()
                .filter(node -> node.getNodeType().equals(NodeTypeEnum.APP.getType()))
                .map(LinkNodeDTO::getNodeName)
                .collect(Collectors.toList());
    }

    /**
     * 根据业务活动ids, 获得应用名称列表
     *
     * @param businessActivityIds 业务活动ids
     * @param applicationName     应用名称
     * @return 应用名称列表
     */
    private Set<String> listApplicationNameByActivityIds(List<Long> businessActivityIds, String applicationName) {
        // 根据业务活动获得应用名称
        return businessActivityIds.stream().map(businessActivityId -> {
            List<String> businessApplicationNames =
                    applicationBusinessActivityService.processAppNameByBusinessActiveId(businessActivityId);

            // 搜索
            if (CollectionUtil.isNotEmpty(businessApplicationNames) && StrUtil.isNotBlank(applicationName)) {
                businessApplicationNames.removeIf(
                        businessApplicationName -> !businessApplicationName.contains(applicationName));
            }

            return businessApplicationNames;

        }).filter(CollectionUtil::isNotEmpty).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /**
     * TODO 变更逻辑后续看如何设计
     *
     * @param oldActivity 旧业务活动
     * @param newActivity 新业务活动
     * @return 是否变更
     */
    private boolean isChange(ActivityResult oldActivity, ActivityUpdateRequest newActivity) {
        return false;
    }

    @Override
    public boolean existsActivity(Long tenantId, String envCode) {
        return activityDAO.existsActivity(tenantId, envCode);
    }
}

