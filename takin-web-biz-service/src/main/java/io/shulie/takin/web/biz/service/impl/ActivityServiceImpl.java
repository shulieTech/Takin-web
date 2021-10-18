package io.shulie.takin.web.biz.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageWrapperVO;
import com.pamirs.takin.entity.domain.vo.scenemanage.TimeVO;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.sdk.model.request.engine.EnginePluginsRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.amdb.api.NotifyClient;
import io.shulie.takin.web.amdb.util.EntranceTypeUtils;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.constant.BusinessActivityRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityQueryRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityVerifyRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.VirtualActivityUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationEntranceTopologyQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityVerifyResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.LinkTopologyService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.biz.utils.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.param.activity.ActivityCreateParam;
import io.shulie.takin.web.data.param.activity.ActivityExistsQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.activity.ActivityUpdateParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.activity.ActivityResult;
import io.shulie.takin.web.ext.entity.UserExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    RedisClientUtils redisClientUtils;
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

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createActivity(ActivityCreateRequest request) {
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
            ActivityUtil.buildEntrance(request.getApplicationName(), request.getMethod(), request.getServiceName(),
                request.getRpcType()));
        activityDAO.createActivity(createParam);
        notifyClient.startApplicationEntrancesCalculate(request.getApplicationName(), request.getServiceName(),
            request.getMethod(), request.getRpcType(), request.getExtend());
    }

    /**
     * 检查正常业务活动
     *
     * @param request 请求参数
     */
    private void checkActivity(ActivityCreateRequest request) {
        ActivityExistsQueryParam param = new ActivityExistsQueryParam();
        param.setType(request.getType());
        param.setActivityName(request.getActivityName());
        List<Long> exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                String.format("保存失败，[名称:%s] 已被使用", request.getActivityName()));
        }

        param = new ActivityExistsQueryParam();
        param.setType(request.getType());
        param.setEntranceName(request.getServiceName());
        param.setApplicationName(request.getApplicationName());
        param.setExtend(request.getExtend());
        param.setMethod(request.getMethod());
        param.setRpcType(request.getRpcType());
        param.setServiceName(request.getServiceName());
        exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                String.format("保存失败，[应用名:%s,类型:%s,入口:%s]已存在",
                    request.getApplicationName(), request.getType().getType(), request.getServiceName()));
        }
    }

    @Override
    public void createVirtualActivity(VirtualActivityCreateRequest request) {
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
            ActivityUtil.buildVirtualEntrance(request.getVirtualEntrance(),
                EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType()));
        //单独字段存中间软件类型
        createParam.setServerMiddlewareType(request.getType());
        activityDAO.createActivityNew(createParam);
    }

    private void checkVirtualActivity(VirtualActivityCreateRequest request) {
        ActivityExistsQueryParam param = new ActivityExistsQueryParam();
        param.setActivityName(request.getActivityName());
        List<Long> exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                String.format("保存失败，[名称:%s] 已被使用", request.getActivityName()));
        }
        param = new ActivityExistsQueryParam();
        param.setVirtualEntrance(request.getVirtualEntrance());
        // rpc转化
        param.setRpcType(EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType());
        exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                String.format("保存失败，[虚拟入口:%s]已存在", request.getVirtualEntrance()));
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
            ActivityUtil.buildVirtualEntrance(request.getVirtualEntrance(),
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
        param.setVirtualEntrance(request.getVirtualEntrance());
        // rpc转化
        param.setRpcType(EntranceTypeUtils.getRpcType(request.getType().getType()).getRpcType());
        List<Long> exists = activityDAO.exists(param);
        if (CollectionUtils.isNotEmpty(exists)) {
            Optional<Long> any = exists.stream().filter(item -> !item.equals(request.getActivityId())).findAny();
            if (any.isPresent()) {
                throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                    String.format("保存失败，虚拟入口已[业务活动：%s：入口：%s]已被使用，对应的ID为：%s", request.getActivityName(),
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
            List<Long> exists = activityDAO.exists(param);
            if (CollectionUtils.isNotEmpty(exists)) {
                Optional<Long> any = exists.stream().filter(item -> !item.equals(activityId)).findAny();
                if (any.isPresent()) {
                    throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                        String.format("保存失败，业务活动[%s]已被使用，对应的ID为：%s", activityName, any.get()));
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
        updateParam.setEntrance(
            ActivityUtil.buildEntrance(request.getApplicationName(), request.getMethod(), request.getServiceName(),
                request.getRpcType()));
        // 技术链路id
        updateParam.setLinkId(oldActivity.getLinkId());
        activityDAO.updateActivity(updateParam);

        // 非核心字段变动，不需要重建链路
        if(StringUtil.equals(request.getApplicationName(), oldActivity.getApplicationName())
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
            List<Long> exists = activityDAO.exists(param);
            if (CollectionUtils.isNotEmpty(exists)) {
                Optional<Long> any = exists.stream()
                    .filter(item -> !item.equals(request.getActivityId()))
                    .findAny();
                if (any.isPresent()) {
                    throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR, String
                        .format("保存失败，入口已[应用名称：%s，类型：%s，入口：%s]已被使用，对应的ID为：%s", request.getActivityName(),
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
        OperationLogContextHolder.addVars(Vars.APPLICATION_NAME, oldActivity.getApplicationName());
        OperationLogContextHolder.addVars(Vars.SERVICE_NAME, oldActivity.getServiceName());
        activityDAO.deleteActivity(activityId);
        // 正常业务活动
        if (oldActivity.getBusinessType().equals(BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
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
        param.setCurrent(request.getCurrent());
        param.setPageSize(request.getPageSize());
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
                WebPluginUtils.fillQueryResponse(response);
                return response;
            }).collect(Collectors.toList());
        return PagingList.of(responses, activityListResultPagingList.getTotal());
    }

    @Override
    public ActivityResponse getActivityById(Long activityId) {
        ActivityResult result = activityDAO.getActivityById(activityId);
        if (result == null) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                activityId + "对应的业务活动不存在");
        }
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setActivityId(result.getActivityId());
        activityResponse.setActivityName(result.getActivityName());
        activityResponse.setType(result.getType());

        if (result.getBusinessType().equals(BusinessTypeEnum.NORMAL_BUSINESS.getType())) {
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
        activityResponse.setIsCore(String.valueOf(result.getIsCore()));
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
            String enableLinkFlowCheckString = ConfigServerHelper.getValueByKey(
                ConfigServerKeyEnum.TAKIN_LINK_FLOW_CHECK_ENABLE);
            activityResponse.setEnableLinkFlowCheck(Boolean.parseBoolean(enableLinkFlowCheckString));

            // 拓扑图查询
            activityResponse.setTopology(linkTopologyService.getApplicationEntrancesTopology(request));
        }

        Integer verifyStatus = this.getVerifyStatus(activityId).getVerifyStatus();
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
        activityResponse.setIsCore(String.valueOf(result.getIsCore()));
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
        String reportId = redisClientUtils.getString(
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
        vo.setPressureTestSceneName(
            activityResult.getActivityName() + BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_SUFFIX);
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
            response.setTaskStatus(false);
            return response;
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
        //taskFlowDebugStartReq.setLicense(WebPluginUtils.getTenantUserAppKey());
        taskFlowDebugStartReq.setCreatorId(WebPluginUtils.traceUserId());
        log.info("流量验证参数：{}", taskFlowDebugStartReq);
        Long startResult = cloudTaskApi.startFlowDebugTask(taskFlowDebugStartReq);
        log.info("流量验证发起结果：{}", startResult.toString());
        response.setTaskStatus(true);
        response.setVerifiedFlag(false);
        response.setVerifyStatus(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFYING);
        //3.缓存任务ID并返回
        redisClientUtils.setString(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY + activityId,
            String.valueOf(startResult), BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY_EXPIRE,
            TimeUnit.SECONDS);
        response.setScriptId(scriptId);
        return response;
    }

    @Override
    public ActivityVerifyResponse getVerifyStatus(Long activityId) {
        ActivityVerifyResponse response = new ActivityVerifyResponse();
        response.setActivityId(activityId);
        response.setVerifyStatus(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_UNVERIFIED);
        //1.从缓存获取taskId
        String reportId = redisClientUtils.getString(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_KEY + activityId);
        //2.根据taskId获取报告状态即任务状态
        if (!StringUtil.isBlank(reportId)) {
            WebResponse webResponse = reportService.getReportByReportId(Long.valueOf(reportId));
            if (Objects.nonNull(webResponse) && Objects.nonNull(webResponse.getData())) {
                ReportDetailOutput reportDetailOutput = (ReportDetailOutput)webResponse.getData();
                Integer verifyStatus = reportDetailOutput.getTaskStatus();
                response.setVerifyStatus(verifyStatus);
                response.setVerifiedFlag(
                    verifyStatus.equals(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_VERIFIED));
            }
        }
        return response;
    }

    // TODO 变更逻辑后续看如何设计
    private boolean isChange(ActivityResult oldActivity, ActivityUpdateRequest newActivity) {
        return false;
    }
}
