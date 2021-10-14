package io.shulie.takin.web.biz.service.scriptmanage.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.common.constant.VerifyTypeEnum;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneScriptRefDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.cloud.open.req.engine.EnginePluginsRefOpen;
import io.shulie.takin.cloud.open.req.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.cloud.open.req.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.amdb.api.TraceClient;
import io.shulie.takin.web.amdb.bean.query.script.QueryLinkDetailDTO;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;
import io.shulie.takin.web.amdb.enums.LinkRequestResultTypeEnum;
import io.shulie.takin.web.biz.constant.ScriptDebugConstants;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskRunWithSaveRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.VerifyTaskConfig;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequestRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.LeakSqlService;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.business.script.ScriptDebugUtil;
import io.shulie.takin.web.biz.utils.business.script.ScriptManageUtil;
import io.shulie.takin.web.biz.utils.exception.ScriptDebugExceptionUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.enums.script.CloudPressureStatus;
import io.shulie.takin.web.common.enums.script.ScriptDebugFailedTypeEnum;
import io.shulie.takin.web.common.enums.script.ScriptDebugStatusEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.common.util.ActivityUtil.EntranceJoinEntity;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.common.vo.LabelValueVO;
import io.shulie.takin.web.common.vo.link.LinkManageTableFeaturesVO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.LinkManageDAO;
import io.shulie.takin.web.data.dao.script.ScriptDebugDAO;
import io.shulie.takin.web.data.dao.scriptmanage.ScriptManageDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.model.mysql.ScriptDebugEntity;
import io.shulie.takin.web.data.model.mysql.ScriptManageDeployEntity;
import io.shulie.takin.web.data.param.scriptmanage.PageScriptDebugParam;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.data.result.linkmange.LinkManageResult;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 脚本调试表(ScriptDebug)表服务实现类
 *
 * @author liuchuan
 * @since 2021-05-10 17:13:18
 */
@Slf4j
@Service
public class ScriptDebugServiceImpl implements ScriptDebugService {

    /**
     * 脚本调试支持的 rpcType mq 下的
     * 以 逗号隔开
     * 默认 kafka, 可以扩展 rocket mq 等..
     * 暂时这么设计
     */
    @Value("${takin-web.script-debug.rpcType:KAFKA}")
    private String supportRpcType;

    @Autowired
    private LeakSqlService leakSqlService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ReportRealTimeService reportRealTimeService;

    @Autowired
    private TraceClient traceClient;

    @Autowired
    private VerifyTaskReportService verifyTaskReportService;

    @Autowired
    private VerifyTaskService verifyTaskService;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    private BusinessLinkManageDAO businessLinkManageDAO;

    @Autowired
    private ScriptDebugDAO scriptDebugDAO;

    @Autowired
    private ScriptManageDAO scriptManageDAO;

    @Autowired
    private SceneTaskService sceneTaskService;

    @Autowired
    private ScriptManageService scriptManageService;

    @Autowired
    @Qualifier("fastDebugThreadPool")
    private ThreadPoolExecutor fastDebugThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private LinkManageDAO linkManageDAO;

    @Resource
    PluginManager pluginManager;

    @Override
    public ScriptDebugResponse debug(ScriptDebugDoDebugRequest request) {
        Long scriptDeployId = request.getScriptDeployId();
        String lockKey = String.format(LockKeyConstants.LOCK_SCRIPT_DEBUG, scriptDeployId);
        if (!distributedLock.tryLockZeroWait(lockKey)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_DEBUG_REPEAT_ERROR, AppConstants.TOO_FREQUENTLY);
        }
        // 增加并发数 与 试跑数判断
        if (request.getConcurrencyNum() > request.getRequestNum()) {
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR, "并发数必须小于等于试跑次数");
        }
        // 响应数据
        ScriptDebugResponse response = new ScriptDebugResponse();
        ScriptDebugEntity scriptDebug;
        try {
            // 脚本发布实例是否存在
            ScriptManageDeployEntity scriptDeploy = scriptManageDAO.getDeployByDeployId(scriptDeployId);
            ScriptDebugExceptionUtil.isDebugError(scriptDeploy == null, "脚本发布实例不存在!");

            // 该脚本发布实例是否有未完成的调试
            ScriptDebugExceptionUtil.isDebugError(scriptDebugDAO.hasUnfinished(scriptDeployId),
                "该脚本有未完成的调试, 请等待调试结束再进行调试!");

            // 根据脚本发布实例类型, 查询业务活动或者业务流程下的业务活动
            // 判断业务流程是否存在, 判断活动是否存在
            List<Long> businessActivityIds = this.listBusinessActivityIdsByScriptDeploy(scriptDeploy);
            ScriptDebugExceptionUtil.isDebugError(businessActivityIds.isEmpty(), "脚本对应的业务活动不存在!");

            // 查出所有的业务活动
            // 根据业务活动ids, 获得业务活动
            List<BusinessLinkManageTableEntity> businessActivities =
                businessLinkManageDAO.listByIds(businessActivityIds);
            ScriptDebugExceptionUtil.isDebugError(businessActivities.isEmpty(), "脚本对应的业务活动不存在!");

            // 检查应用相关, rpc 检查
            log.info("调试 --> 检测所有业务活动的配置!");
            String applicationErrorMessage = this.checkBusinessActivityCorrelationAndGetError(businessActivities);
            if (StrUtil.isNotBlank(applicationErrorMessage)) {
                response.setErrorMessages(Collections.singletonList(applicationErrorMessage));
                return response;
            }

            // 构建启动调试入参
            log.info("调试 --> 构建 调用 cloud 启动 入参!");
            Integer requestNum = request.getRequestNum();
            Integer concurrencyNum = request.getConcurrencyNum();
            SceneTryRunTaskStartReq debugCloudRequest =
                this.getDebugParams(scriptDeploy, businessActivities, requestNum, concurrencyNum);

            // 脚本检查
            log.info("调试 --> 脚本校验!");
            List<String> errorMessages = this.checkScriptCorrelationAndGetError(debugCloudRequest);
            if (!errorMessages.isEmpty()) {
                response.setErrorMessages(errorMessages);
                return response;
            }
            //填充当前用户信息为操作人
            UserExt user = WebPluginUtils.getUser();
            if (user != null) {
                debugCloudRequest.setOperateId(user.getId());
                debugCloudRequest.setOperateName(user.getName());
            }
            // 启动调试
            SceneTryRunTaskStartResp cloudResponse = this.doDebug(debugCloudRequest);

            // 创建调试记录
            log.info("调试 --> 创建调试记录!");
            response = new ScriptDebugResponse();
            scriptDebug = this.createScriptDebugAndGet(scriptDeployId, requestNum, concurrencyNum, cloudResponse,
                businessActivityIds);
            response.setScriptDebugId(scriptDebug.getId());

            //回写调试记录ID到流量账户
            callBackToWriteBalance(cloudResponse, scriptDebug.getId());

            log.info("调试 --> 异步启动循环查询启动成功, 压测完成!");
            fastDebugThreadPool.execute(this.checkPressureStatus(scriptDebug));

            log.info("调试 --> 接口完成!");
            return response;
        } catch (Exception e) {
            log.error("调试 --> 发生错误 --> 错误信息: {}", e.getMessage(), e);
            response.setErrorMessages(Collections.singletonList(e.getMessage()));
            return response;
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    private void callBackToWriteBalance(SceneTryRunTaskStartResp cloudResponse, Long scriptDebugId) {
        ScriptAssetBalanceReq req = new ScriptAssetBalanceReq();
        req.setScriptDebugId(scriptDebugId);
        req.setCloudReportId(cloudResponse.getReportId());
        sceneTaskApi.callBackToWriteBalance(req);
    }

    /**
     * 脚本检查
     *
     * @param debugCloudRequest 相关入参
     * @return 错误信息
     */
    private List<String> checkScriptCorrelationAndGetError(SceneTryRunTaskStartReq debugCloudRequest) {
        SceneManageWrapperDTO sceneData = new SceneManageWrapperDTO();
        sceneData.setScriptType(debugCloudRequest.getScriptType());

        // 上传路径
        List<SceneScriptRefOpen> uploadFileList = debugCloudRequest.getUploadFile();
        if (CollectionUtil.isNotEmpty(uploadFileList)) {
            List<SceneScriptRefDTO> uploadFileDTOList = uploadFileList.stream().map(uploadFile -> {
                SceneScriptRefDTO sceneScriptRefDTO = new SceneScriptRefDTO();
                sceneScriptRefDTO.setFileType(uploadFile.getFileType());
                sceneScriptRefDTO.setIsDeleted(AppConstants.NO);
                sceneScriptRefDTO.setId(uploadFile.getId());
                sceneScriptRefDTO.setUploadPath(uploadFile.getUploadPath());
                return sceneScriptRefDTO;
            }).collect(Collectors.toList());
            sceneData.setUploadFile(uploadFileDTOList);
        }

        List<SceneBusinessActivityRefOpen> businessActivityConfigList = debugCloudRequest.getBusinessActivityConfig();
        List<SceneBusinessActivityRefDTO> businessActivityConfigDTOList = businessActivityConfigList.stream().map(
            businessActivityConfig -> {
                SceneBusinessActivityRefDTO sceneBusinessActivityRefDTO = new SceneBusinessActivityRefDTO();
                sceneBusinessActivityRefDTO.setBusinessActivityId(businessActivityConfig.getBusinessActivityId());
                sceneBusinessActivityRefDTO.setBusinessActivityName(businessActivityConfig.getBusinessActivityName());
                return sceneBusinessActivityRefDTO;
            }).collect(Collectors.toList());

        sceneData.setBusinessActivityConfig(businessActivityConfigDTOList);
        sceneData.setIsAbsoluteScriptPath(true);
        String result = sceneTaskService.checkScriptCorrelation(sceneData);
        return Arrays.asList(StrUtil.split(result, Constants.SPLIT));
    }

    /**
     * 检查业务活动相关, 并获得错误
     *
     * @param businessActivities 业务活动列表
     * @return 错误信息
     */
    private String checkBusinessActivityCorrelationAndGetError(List<BusinessLinkManageTableEntity> businessActivities) {
        // 然后获得应用程序列表
        List<String> applicationNames = businessActivities.stream().map(businessActivity -> {
            if (!ActivityUtil.isNormalBusiness(businessActivity.getType())) {
                return "";
            }

            // rpcType 判断
            ScriptDebugExceptionUtil.isDebugError(!this.checkBusinessActivityRpcType(businessActivity),
                String.format("脚本调试暂时支持 http, %s 的业务活动!", supportRpcType));

            // 应用名称获得
            return ActivityUtil.covertEntrance(businessActivity.getEntrace()).getApplicationName();
        }).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        // 没有绑定应用, 不校验
        if (applicationNames.isEmpty()) {
            return "";
        }

        // 查询应用列表
        List<ApplicationMntEntity> applications = applicationDAO.listByApplicationNamesAndCustomerId(applicationNames);
        if (applications.isEmpty()) {
            return "业务活动对应的应用程序不存在!";
        }

        // 检查
        List<TApplicationMnt> tApplicationList = applications.stream().map(application -> {
            TApplicationMnt tApplicationMnt = new TApplicationMnt();
            tApplicationMnt.setApplicationName(application.getApplicationName());
            tApplicationMnt.setApplicationId(application.getApplicationId());
            tApplicationMnt.setAccessStatus(application.getAccessStatus());
            tApplicationMnt.setSwitchStatus(application.getSwitchStatus());
            tApplicationMnt.setNodeNum(application.getNodeNum());
            return tApplicationMnt;
        }).collect(Collectors.toList());
        String errorMessage = sceneTaskService.checkApplicationCorrelation(tApplicationList);
        return StrUtil.isNotBlank(errorMessage)
            ? errorMessage.replace(Constants.SPLIT, "")
            : errorMessage;
    }

    @Override
    public ScriptDebugDetailResponse getById(Long scriptDebugId) {
        ScriptDebugEntity scriptDebugEntity = scriptDebugDAO.getById(scriptDebugId);
        ScriptDebugExceptionUtil.isDebugError(scriptDebugEntity == null, "调试记录不存在!");
        ScriptDebugDetailResponse response = new ScriptDebugDetailResponse();
        BeanUtils.copyProperties(scriptDebugEntity, response);
        // 如果漏数状态是 99, 返回 null, 前端判断不展示!
        if (ScriptDebugUtil.noLeakConfig(scriptDebugEntity.getLeakStatus())) {
            response.setLeakStatus(null);
        }

        // 调试记录关联的脚本发布实例关联的业务活动列表
        List<LabelValueVO> businessActivitiesVO = Collections.emptyList();
        response.setBusinessActivities(businessActivitiesVO);

        // 业务活动
        // 调试记录下的 脚本发布id
        Long scriptDeployId = scriptDebugEntity.getScriptDeployId();
        ScriptManageDeployEntity scriptDeploy = scriptManageDAO.getDeployByDeployId(scriptDeployId);
        if (scriptDeploy == null) {
            return response;
        }

        // 获得业务活动ids
        List<Long> businessActivityIds = this.checkAndListBusinessActivityIds(scriptDeploy);
        if (businessActivityIds.isEmpty()) {
            return response;
        }

        // 获得业务活动列表
        List<BusinessLinkManageTableEntity> businessActivities = businessLinkManageDAO.listByIds(businessActivityIds);
        if (businessActivities.isEmpty()) {
            return response;
        }

        // 拼接 label, value
        businessActivitiesVO = businessActivities.stream()
            .map(businessActivity -> {
                LabelValueVO vo = new LabelValueVO();
                vo.setLabel(businessActivity.getLinkName());
                vo.setValue(businessActivity.getLinkId());
                return vo;
            }).collect(Collectors.toList());
        response.setBusinessActivities(businessActivitiesVO);
        return response;
    }

    @Override
    public PagingList<ScriptDebugListResponse> pageFinishedByScriptDeployId(
        PageScriptDebugRequest pageScriptDebugRequest) {
        // 脚本发布实例下的调试记录, 只查询完成的调试记录
        List<Integer> finishedStatusList = Arrays.asList(ScriptDebugStatusEnum.SUCCESS.getCode(),
            ScriptDebugStatusEnum.FAILED.getCode());

        PageScriptDebugParam pageScriptDebugParam = new PageScriptDebugParam();
        BeanUtils.copyProperties(pageScriptDebugRequest, pageScriptDebugParam);
        pageScriptDebugParam.setScriptDeployId(pageScriptDebugRequest.getScriptDeployId());
        pageScriptDebugParam.setStatusList(finishedStatusList);
        IPage<ScriptDebugEntity> scriptDebugPage = scriptDebugDAO.pageByScriptDeployIdAndStatusList(
            pageScriptDebugParam);
        List<ScriptDebugEntity> records = scriptDebugPage.getRecords();
        if (records.isEmpty()) {
            return PagingList.of(Collections.emptyList(), scriptDebugPage.getTotal());
        }

        List<ScriptDebugListResponse> responseList = records.stream().map(scriptDebug -> {
            ScriptDebugListResponse response = new ScriptDebugListResponse();
            BeanUtils.copyProperties(scriptDebug, response);
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responseList, scriptDebugPage.getTotal());
    }

    @Override
    public PagingList<ScriptDebugRequestListResponse> pageScriptDebugRequest(PageScriptDebugRequestRequest request) {
        Long scriptDebugId = request.getScriptDebugId();
        ScriptDebugEntity scriptDebugEntity = scriptDebugDAO.getById(scriptDebugId);
        ScriptDebugExceptionUtil.isRequestListError(scriptDebugEntity == null, "调试记录不存在!");

        // 拼接入参
        QueryLinkDetailDTO queryLinkDetailDTO = new QueryLinkDetailDTO();
        queryLinkDetailDTO.setCurrentPage(request.getRealCurrent());
        queryLinkDetailDTO.setPageSize(request.getPageSize());
        queryLinkDetailDTO.setTaskId(scriptDebugEntity.getCloudReportId().toString());
        queryLinkDetailDTO.setStartTime(scriptDebugEntity.getCreatedAt().getTime());
        queryLinkDetailDTO.setEndTime(scriptDebugEntity.getUpdatedAt().getTime());

        // 业务活动入参筛选
        Long businessActivityId = request.getBusinessActivityId();
        if (businessActivityId != null) {
            // 查询, 判断
            BusinessLinkResult businessLinkResult = businessLinkManageDAO.selectBussinessLinkById(businessActivityId);
            ScriptDebugExceptionUtil.isRequestListError(businessLinkResult == null, "业务活动不存在!");

            // 取 serviceName, 赋值
            String entrance = businessLinkResult.getEntrace();
            EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(entrance);
            queryLinkDetailDTO.setServiceName(entranceJoinEntity.getServiceName());
        }

        // entryList
        List<EntranceRuleDTO> entryList = this.getEntryList(scriptDebugEntity.getScriptDeployId(),
            request.getBusinessActivityId());
        if (entryList.isEmpty()) {
            return PagingList.empty();
        }
        queryLinkDetailDTO.setEntranceRuleDTOS(entryList);

        // 根据 入参 type, 转换为 resultType
        Integer type = request.getType();
        if (type != null) {
            // 响应失败
            if (ScriptDebugConstants.REQUEST_TYPE_FAILED == type) {
                queryLinkDetailDTO.setResultTypeInt(LinkRequestResultTypeEnum.FAILED.getCode());
            }

            // 断言失败
            if (ScriptDebugConstants.REQUEST_TYPE_FAILED_ASSERT == type) {
                queryLinkDetailDTO.setResultTypeInt(LinkRequestResultTypeEnum.FAILED_ASSERT.getCode());
            }
        }

        // 调用大数据
        PagingList<EntryTraceInfoDTO> entryTracePage = traceClient.listEntryTraceByTaskId(queryLinkDetailDTO);
        List<EntryTraceInfoDTO> list = entryTracePage.getList();
        if (CollectionUtils.isEmpty(list)) {
            return PagingList.empty();
        }

        // 组装
        List<ScriptDebugRequestListResponse> requestList = list.stream().map(dto -> {
            ScriptDebugRequestListResponse response = new ScriptDebugRequestListResponse();
            response.setEntry(dto.getServiceName());
            response.setTraceId(dto.getTraceId());
            response.setRequestAt(LocalDateTimeUtil.of(dto.getStartTime()));
            response.setRequestBody(dto.getRequest());
            response.setResponseBody(dto.getResponse());

            // resultCode 判断, 赋值
            ScriptDebugRequestListResponse requestListStatusResponse = ScriptDebugUtil.getRequestListStatusResponse(
                dto.getResultCode(), dto.getAssertResult());

            response.setResponseStatus(requestListStatusResponse.getResponseStatus());
            response.setResponseStatusDesc(requestListStatusResponse.getResponseStatusDesc());
            response.setAssertDetailList(requestListStatusResponse.getAssertDetailList());

            return response;
        }).collect(Collectors.toList());

        return PagingList.of(requestList, entryTracePage.getTotal());
    }

    /**
     * 获得 entryList
     *
     * @param scriptDeployId     脚本发布id
     * @param businessActivityId 业务活动id
     * @return entryList
     */
    private List<EntranceRuleDTO> getEntryList(Long scriptDeployId, Long businessActivityId) {
        // 脚本发布实例是否存在
        ScriptManageDeployEntity scriptDeploy = scriptManageDAO.getDeployByDeployId(scriptDeployId);
        ScriptDebugExceptionUtil.isCommonError(scriptDeploy == null, "脚本发布实例不存在!");

        List<Long> businessActivityIds = this.listBusinessActivityIdsByScriptDeploy(scriptDeploy);
        if (businessActivityId != null && businessActivityIds.contains(businessActivityId)) {
            businessActivityIds = Collections.singletonList(businessActivityId);
        }

        // ids 不存在, 返回空
        if (businessActivityIds.isEmpty()) {
            return Collections.emptyList();
        }

        return reportRealTimeService.getEntryListByBusinessActivityIds(businessActivityIds);
    }

    /**
     * 通过脚本发布实例详情, 获得并检查业务活动ids
     *
     * @param scriptDeploy 脚本发布实例详情
     * @return 业务活动ids
     */
    private List<Long> checkAndListBusinessActivityIds(ScriptManageDeployEntity scriptDeploy) {
        List<Long> businessActivityIds = this.listBusinessActivityIdsByScriptDeploy(scriptDeploy);
        ScriptDebugExceptionUtil.isDebugError(businessActivityIds.isEmpty(), "脚本对应的业务活动不存在!");
        return businessActivityIds;
    }

    /**
     * 创建脚本调试记录并获得调试记录id
     *
     * @param scriptDeployId      脚本发布id
     * @param requestNum          请求条数
     * @param cloudResponse       cloud 接口返回的信息
     * @param businessActivityIds 业务活动ids
     * @return 调试记录id
     */
    private ScriptDebugEntity createScriptDebugAndGet(Long scriptDeployId, Integer requestNum,
        Integer concurrencyNum, SceneTryRunTaskStartResp cloudResponse, List<Long> businessActivityIds) {
        ScriptDebugEntity scriptDebugEntity = new ScriptDebugEntity();
        scriptDebugEntity.setScriptDeployId(scriptDeployId);
        scriptDebugEntity.setStatus(ScriptDebugStatusEnum.NOT_START.getCode());
        scriptDebugEntity.setRequestNum(requestNum);
        // 增加并发数
        scriptDebugEntity.setConcurrencyNum(concurrencyNum);
        scriptDebugEntity.setCloudReportId(cloudResponse.getReportId());
        scriptDebugEntity.setCloudSceneId(cloudResponse.getSceneId());
        // 创建时间
        scriptDebugEntity.setCreatedAt(DateUtil.date());

        // 查看是否配置漏数
        LeakSqlBatchRefsRequest refsRequest = new LeakSqlBatchRefsRequest();
        refsRequest.setBusinessActivityIds(businessActivityIds);
        List<VerifyTaskConfig> verifyTaskConfigList = leakSqlService.getVerifyTaskConfig(refsRequest);
        // 如果是空的, 说明没有配置漏数配置, 不用进行漏数检查
        if (CollectionUtils.isEmpty(verifyTaskConfigList)) {
            scriptDebugEntity.setLeakStatus(ScriptDebugConstants.NO_LEAK_CONFIG);
        }

        ScriptDebugExceptionUtil.isDebugError(!scriptDebugDAO.save(scriptDebugEntity), "调试记录创建失败!");
        return scriptDebugEntity;
    }

    /**
     * 获得脚本发布实例下的所有关联的业务
     *
     * @param scriptDeploy 脚本发布实例
     * @return 业务活动ids
     */
    private List<Long> listBusinessActivityIdsByScriptDeploy(ScriptManageDeployEntity scriptDeploy) {
        // 脚本发布实例关联业务活动, 直接返回id
        if (ScriptManageUtil.deployRefBusinessActivityType(scriptDeploy.getRefType())) {
            return Collections.singletonList(Long.valueOf(scriptDeploy.getRefValue()));
        }

        // 关联业务流程, 查出业务流程关联业务活动, 然后返回ids
        if (ScriptManageUtil.deployRefBusinessFlowType(scriptDeploy.getRefType())) {
            // 业务流程id
            Long businessFlowId = Long.valueOf(scriptDeploy.getRefValue());
            return businessLinkManageDAO.listIdsByBusinessFlowId(businessFlowId);
        }

        return Collections.emptyList();
    }

    /**
     * 异步启动, 轮询调用压测状态
     * 更新调试记录状态
     *
     * @param scriptDebug 调试记录
     * @return 可运行
     */
    private Runnable checkPressureStatus(ScriptDebugEntity scriptDebug) {
        return () -> {
            // 准备更新的调试记录
            ScriptDebugEntity newScriptDebug = new ScriptDebugEntity();
            newScriptDebug.setId(scriptDebug.getId());

            try {
                // 脚本变动更新
                this.updateScriptDebugIngStatus(scriptDebug, newScriptDebug);

                // 超时检查
                this.checkTimeout(newScriptDebug);

                // 是否失败检查
                ScriptManageDeployEntity scriptDeploy = scriptManageDAO.getDeployByDeployId(
                    scriptDebug.getScriptDeployId());
                if (scriptDeploy == null) {
                    newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
                    newScriptDebug.setRemark("脚本发布不存在!");
                    this.updateScriptDebugAndCheck(newScriptDebug);
                    return;
                }

                // 需要等待 5s, 等 agent 完全上报完数据
                TimeUnit.SECONDS.sleep(5);

                newScriptDebug.setCloudReportId(scriptDebug.getCloudReportId());
                newScriptDebug.setLeakStatus(scriptDebug.getLeakStatus());
                // 漏数检查
                this.checkLeak(newScriptDebug, scriptDeploy.getRefValue(), scriptDeploy.getRefType());

                // 非 200 检查
                newScriptDebug.setCreatedAt(scriptDebug.getCreatedAt());
                newScriptDebug.setScriptDeployId(scriptDebug.getScriptDeployId());
                this.checkOther(newScriptDebug);

                // 更新为成功状态
                if (ScriptDebugUtil.isFinished(newScriptDebug.getStatus())) {
                    return;
                }

                log.info("调试 --> 检查压测状态 --> 检查完成, 更新为成功状态!");
                newScriptDebug.setStatus(ScriptDebugStatusEnum.SUCCESS.getCode());
            } catch (Exception e) {
                log.error("调试 --> 更新状态过程失败, 错误信息: {}", e.getMessage(), e);
                newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
            }

            this.updateScriptDebugAndCheck(newScriptDebug);
        };
    }

    /**
     * 失败检查之其他检查
     * 非 200, 以及之后的扩展
     *
     * @param newScriptDebug 需更新的实例
     */
    private void checkOther(ScriptDebugEntity newScriptDebug) {
        // 失败状态, 直接返回
        if (ScriptDebugUtil.isFailed(newScriptDebug.getStatus())) {
            return;
        }

        log.info("调试 --> 检查压测状态 --> 其他检查开始!");

        // 非200检查
        // 查询记录, 有非200的记录, 就是调试失败
        try {
            QueryLinkDetailDTO dto = new QueryLinkDetailDTO();
            dto.setResultTypeInt(LinkRequestResultTypeEnum.FAILED.getCode());
            dto.setTaskId(newScriptDebug.getCloudReportId().toString());
            dto.setPageSize(1);
            dto.setEntranceRuleDTOS(this.getEntryList(newScriptDebug.getScriptDeployId(), null));
            dto.setStartTime(newScriptDebug.getCreatedAt().getTime());
            dto.setEndTime(System.currentTimeMillis());
            PagingList<EntryTraceInfoDTO> entryTracePage = traceClient.listEntryTraceByTaskId(dto);

            if (entryTracePage.getTotal() != 0) {
                newScriptDebug.setFailedType(ScriptDebugFailedTypeEnum.FAILED_RESPONSE.getCode());
                newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
                newScriptDebug.setRemark("调试非200错误");
                this.updateScriptDebugAndCheck(newScriptDebug);
            }
        } catch (Exception e) {
            newScriptDebug.setFailedType(ScriptDebugFailedTypeEnum.FAILED_OTHER.getCode());
            newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
            newScriptDebug.setRemark("其他检查发生错误; " + e.getMessage());
            log.error("调试 --> 检查压测状态 --> 其他检查 --> 发生错误, 错误信息: {}", e.getMessage(), e);
            this.updateScriptDebugAndCheck(newScriptDebug);
        }

        log.info("调试 --> 检查压测状态 --> 其他检查完成!");
    }

    /**
     * 失败检查之漏数检查
     *
     * @param newScriptDebug 需更新的实例
     * @param refValue       脚本发布关联的业务id
     * @param refType        脚本发布关联的业务类型, 业务活动或者业务流程
     */
    public void checkLeak(ScriptDebugEntity newScriptDebug, String refValue, String refType) {
        // 没有漏数配置, 或者 失败状态, 直接返回
        if (ScriptDebugUtil.noLeakConfig(newScriptDebug.getLeakStatus())
            || ScriptDebugUtil.isFailed(newScriptDebug.getStatus())) {
            return;
        }

        log.info("调试 --> 检查压测状态 --> 漏数检查!");

        // 漏数检查
        try {
            LeakVerifyTaskRunWithSaveRequest runRequest = new LeakVerifyTaskRunWithSaveRequest();
            runRequest.setReportId(newScriptDebug.getCloudReportId());
            runRequest.setRefId(Long.valueOf(refValue));
            runRequest.setRefType(this.getVerifyType(refType));
            verifyTaskService.runWithResultSave(runRequest);

            LeakVerifyTaskReportQueryRequest queryLeakRequest = new LeakVerifyTaskReportQueryRequest();
            queryLeakRequest.setReportId(newScriptDebug.getCloudReportId());
            LeakVerifyTaskResultResponse queryLeakResponse = verifyTaskReportService.getVerifyTaskReport(
                queryLeakRequest);
            // 返回结果不存在, 或者漏数检测不是正常状态
            if (queryLeakResponse == null) {
                newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
                newScriptDebug.setRemark("漏数检测失败!");
                newScriptDebug.setLeakStatus(VerifyResultStatusEnum.UNCHECK.getCode());
            } else if (!VerifyResultStatusEnum.NORMAL.getCode().equals(queryLeakResponse.getStatus())) {
                newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
                newScriptDebug.setRemark(queryLeakResponse.getStatusResponse().getLabel());
                newScriptDebug.setLeakStatus(queryLeakResponse.getStatus());
            } else {
                newScriptDebug.setLeakStatus(queryLeakResponse.getStatus());
            }
        } catch (Exception e) {
            newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
            newScriptDebug.setRemark("漏数检查发生错误; " + e.getMessage());
            newScriptDebug.setLeakStatus(VerifyResultStatusEnum.UNCHECK.getCode());

            log.error("调试 --> 检查压测状态 --> 漏数检查 --> 发生错误, 错误信息: {}", e.getMessage(), e);
        }

        // 更新
        newScriptDebug.setFailedType(ScriptDebugFailedTypeEnum.FAILED_LEAK.getCode());
        this.updateScriptDebugAndCheck(newScriptDebug);
        log.info("调试 --> 检查压测状态 --> 漏数检查完成!");
    }

    /**
     * 根据脚本里的 refType
     * 获得 verifyType
     * 它们是反的
     *
     * @param refType 脚本发布里对应的业务类型, 活动或流程
     * @return verifyType
     */
    private Integer getVerifyType(String refType) {
        // 脚本里是 业务活动
        if (ScriptManageUtil.deployRefBusinessActivityType(refType)) {
            return VerifyTypeEnum.ACTIVITY.getCode();
        }

        // 脚本里是 业务流程
        if (ScriptManageUtil.deployRefBusinessFlowType(refType)) {
            return VerifyTypeEnum.FLOW.getCode();
        }

        return VerifyTypeEnum.ACTIVITY.getCode();
    }

    /**
     * 超时失败, 更新状态
     *
     * @param newScriptDebug 需更新的实例
     */
    private void checkTimeout(ScriptDebugEntity newScriptDebug) {
        // 失败状态, 直接返回
        if (ScriptDebugUtil.isFailed(newScriptDebug.getStatus())) {
            return;
        }

        log.info("调试 --> 检查压测状态 --> 超时检查!");

        // 如果不是请求完成, 那么就是超时了
        if (!ScriptDebugUtil.isRequestEnd(newScriptDebug.getStatus())) {
            newScriptDebug.setStatus(ScriptDebugStatusEnum.FAILED.getCode());
            newScriptDebug.setRemark("压测未有响应，请重试，或者联系技术人员");
            newScriptDebug.setFailedType(ScriptDebugFailedTypeEnum.FAILED_TIMEOUT.getCode());
            this.updateScriptDebugAndCheck(newScriptDebug);
        }

        log.info("调试 --> 检查压测状态 --> 超时检查完成!");
    }

    /**
     * 更新调试记录, 并判断
     *
     * @param scriptDebug 调试记录对象
     */
    private void updateScriptDebugAndCheck(ScriptDebugEntity scriptDebug) {
        String remark = scriptDebug.getRemark();
        if (!StringUtils.isEmpty(remark) && remark.length() > 490) {
            scriptDebug.setRemark(remark.substring(0, 490));
        }

        if (!scriptDebugDAO.updateById(scriptDebug)) {
            log.error("检查压测结果 --> 更新调试记录状态失败! 状态为: {}", scriptDebug.getStatus());
            throw ScriptDebugExceptionUtil.getCheckStatusError("更新调试记录状态失败!");
        }
    }

    /**
     * 调试记录变化中的状态 更新
     * 此方法会赋值新的状态给 newScriptDebug
     *
     * @param scriptDebug    调试记录实例
     * @param newScriptDebug 用于更新的调试记录
     */
    private void updateScriptDebugIngStatus(ScriptDebugEntity scriptDebug, ScriptDebugEntity newScriptDebug) {
        log.info("调试 --> 检查压测状态 --> cloud 压测检查!");

        // 拼接入参
        SceneTryRunTaskCheckReq checkRequest = new SceneTryRunTaskCheckReq();
        checkRequest.setReportId(scriptDebug.getCloudReportId());
        checkRequest.setSceneId(scriptDebug.getCloudSceneId());
        log.info("调试 --> 检查压测状态 --> cloud 压测检查 --> 请求入参: {}!", JSONUtil.toJsonStr(checkRequest));

        // 重试40次, 每次等待2s, 加上接口请求响应时间, 大于80s
        int retryTime = 40;
        int intervalTime = 2;
        for (int i = 1; i <= retryTime; i++) {
            try {
                CloudPressureStatus taskStatusEnum = null;

                // 每次请求, 最大10分钟, 防止一直 running
                // 单位 毫秒, aka 600秒, 10分钟
                int during = 600000;
                // 初始时间, 单位 毫秒
                long initTime = System.currentTimeMillis();

                do {
                    ResponseResult<SceneTryRunTaskStatusResp> response = sceneTaskApi.checkTryRunTaskStatus(
                        checkRequest);
                    log.info("调试 --> 检查压测状态 --> cloud 压测检查 --> 出参: {}!", JSONUtil.toJsonStr(response));

                    // 如果接口失败, 记录日志, 继续轮询
                    if (response == null) {
                        log.error("检查压测结果 --> 第{}次调用, 失败! 错误信息: 无响应数据", i);

                    } else if (!response.getSuccess()) {
                        log.error("检查压测结果 --> 第{}次调用, 失败! 错误信息: {}", i, response.getError());

                    } else {
                        // 接口成功, 判断状态
                        SceneTryRunTaskStatusResp result = response.getData();
                        if (result == null) {
                            log.error("检查压测结果 --> 第{}次调用, 失败! 错误信息: {}", i, "没有返回约定的信息");
                        } else {
                            taskStatusEnum = CloudPressureStatus.getByStatus(result.getTaskStatus());
                            if (taskStatusEnum == null) {
                                break;
                            }

                            // 根据状态更新调试记录
                            this.updateScriptDebugStatusByPressureStatus(newScriptDebug, result, taskStatusEnum);
                        }
                    }

                    // 间隔时间
                    TimeUnit.SECONDS.sleep(intervalTime);

                    // 当前时间减去初始时间, 如果大于间隔时间, 跳出循环
                    if (System.currentTimeMillis() - initTime > during) {
                        break;
                    }
                } while (taskStatusEnum != null && taskStatusEnum.equals(CloudPressureStatus.RUNNING));

                // 如果是请求完成, 或者失败, 就结束调用
                if (ScriptDebugUtil.isRequestEnd(newScriptDebug.getStatus()) ||
                    ScriptDebugUtil.isFailed(newScriptDebug.getStatus())) {
                    break;
                }
            } catch (Exception e) {
                log.error("检查压测结果 --> 第{}次调用, 失败! 错误信息: {}", i, e.getMessage(), e);
            }
        }

        log.info("调试 --> 检查压测状态 --> cloud 压测检查完成!");
    }

    /**
     * 更新调试记录状态
     *
     * @param scriptDebug    调试记录
     * @param result         压测状态结果
     * @param taskStatusEnum cloud 压测状态枚举
     */
    private void updateScriptDebugStatusByPressureStatus(ScriptDebugEntity scriptDebug,
        SceneTryRunTaskStatusResp result, CloudPressureStatus taskStatusEnum) {
        Integer status = this.getStatusByTaskStatusEnum(taskStatusEnum);
        if (status == null) {
            return;
        }

        log.info("调试 --> 检查压测状态 --> 两方状态, taskStatus: {}, status: {}!", result.getTaskStatus(), status);
        // 如果转换后的状态还是等于上次更新过的状态, 直接跳过
        if (status.equals(scriptDebug.getStatus())) {
            return;
        }

        scriptDebug.setStatus(status);
        if (ScriptDebugStatusEnum.FAILED.getCode().equals(status)) {
            scriptDebug.setRemark(result.getErrorMsg());
        }

        // 更新
        this.updateScriptDebugAndCheck(scriptDebug);
    }

    /**
     * 根据压测状态获取调试记录状态
     *
     * @param taskStatusEnum 压测状态
     * @return 调试记录状态
     */
    private Integer getStatusByTaskStatusEnum(CloudPressureStatus taskStatusEnum) {
        Integer status;
        switch (taskStatusEnum) {
            case STARTING:
                status = ScriptDebugStatusEnum.STARTING.getCode();
                break;

            case STARTED:
                status = ScriptDebugStatusEnum.REQUESTING.getCode();
                break;

            case FAILED:
                status = ScriptDebugStatusEnum.FAILED.getCode();
                break;

            case ENDED:
                status = ScriptDebugStatusEnum.REQUEST_END.getCode();
                break;

            default:
                status = null;
        }

        return status;
    }

    /**
     * 调用 cloud 启动调试
     *
     * @param debugCloudRequest 启动所需参数
     * @return 启动后返回的数据
     */
    private SceneTryRunTaskStartResp doDebug(SceneTryRunTaskStartReq debugCloudRequest) {
        // 启动
        log.info("调试 --> 调用 cloud 启动, 入参: {}", JSONUtil.toJsonStr(debugCloudRequest));
        ResponseResult<SceneTryRunTaskStartResp> result = sceneTaskApi.startTryRunTask(debugCloudRequest);
        log.info("调试 --> 调用 cloud 启动, 出参: {}", JSONUtil.toJsonStr(result));

        if (!result.getSuccess()) {
            log.error("调试接口错误 --> 调用 cloud 接口错误: {}", result.getError());
            throw ScriptDebugExceptionUtil.getDebugError(result.getError().getMsg());
        }

        SceneTryRunTaskStartResp response = result.getData();
        if (response == null) {
            log.error("调试接口错误 --> cloud 接口没有返回约定信息!");
            throw ScriptDebugExceptionUtil.getDebugError(result.getError().getMsg());
        }

        log.info("调试 --> 调用 cloud 启动成功!");
        return response;
    }

    /**
     * 构建启动调试的入参
     *
     * @param scriptDeploy       脚本发布对象
     * @param businessActivities 业务活动列表
     * @param requestNum         请求条数
     * @return 入参
     */
    private SceneTryRunTaskStartReq getDebugParams(ScriptManageDeployEntity scriptDeploy,
        List<BusinessLinkManageTableEntity> businessActivities, Integer requestNum, Integer concurrencyNum) {
        // cloud 调试请求参数拼接
        SceneTryRunTaskStartReq debugCloudRequest = new SceneTryRunTaskStartReq();
        // 脚本发布id
        Long scriptDeployId = scriptDeploy.getId();
        debugCloudRequest.setLoopsNum(requestNum);
        debugCloudRequest.setScriptDeployId(scriptDeployId);
        // 增加并发数
        debugCloudRequest.setConcurrencyNum(concurrencyNum);
        debugCloudRequest.setScriptId(scriptDeploy.getScriptId());
        debugCloudRequest.setScriptType(scriptDeploy.getType());
        debugCloudRequest.setScriptName(scriptDeploy.getName());
        // 插件ids
        List<PluginConfigDetailResponse> pluginConfigs = ScriptManageUtil.listPluginConfigs(scriptDeploy.getFeature());
        if (CollectionUtils.isNotEmpty(pluginConfigs)) {
            List<Long> pluginIds = pluginConfigs.stream()
                .map(o -> Long.valueOf(o.getName()))
                .collect(Collectors.toList());
            debugCloudRequest.setEnginePluginIds(pluginIds);
            debugCloudRequest.setEnginePlugins(pluginConfigs.stream()
                .map(detail -> new EnginePluginsRefOpen() {{
                    setPluginId(Long.parseLong(detail.getName()));
                    setVersion(detail.getVersion());
                }}).collect(Collectors.toList()));
        }

        // 业务活动配置
        debugCloudRequest.setBusinessActivityConfig(
            this.listBusinessActivityConfigList(scriptDeployId, businessActivities));

        // 上传文件
        debugCloudRequest.setUploadFile(this.listUploadPathList(scriptDeployId));

        Map<String, Object> featuresMap = new HashMap<>(2);
        featuresMap.put("scriptId", scriptDeployId);
        debugCloudRequest.setFeatures(JSONUtil.toJsonStr(featuresMap));
        return debugCloudRequest;
    }

    /**
     * 脚本发布实例下的所有文件
     *
     * @param scriptDeployId 脚本发布id
     * @return 文件列表
     */
    private List<SceneScriptRefOpen> listUploadPathList(Long scriptDeployId) {
        ScriptManageDeployDetailResponse scriptDeployResponse = new ScriptManageDeployDetailResponse();
        scriptDeployResponse.setId(scriptDeployId);
        scriptManageService.setFileList(scriptDeployResponse);
        return ScriptManageUtil.buildScriptRef(scriptDeployResponse);
    }

    /**
     * 获得脚本发布下的业务活动配置
     *
     * @param scriptDeployId     脚本发布id
     * @param businessActivities 业务活动列表
     * @return 业务活动配置列表
     */
    private List<SceneBusinessActivityRefOpen> listBusinessActivityConfigList(Long scriptDeployId,
        List<BusinessLinkManageTableEntity> businessActivities) {
        // vo
        List<SceneBusinessActivityRefVO> voList = businessActivities.stream().map(businessActivity -> {
            SceneBusinessActivityRefVO vo = new SceneBusinessActivityRefVO();
            vo.setBusinessActivityId(businessActivity.getLinkId());
            vo.setBusinessActivityName(businessActivity.getLinkName());
            vo.setScriptId(scriptDeployId);
            return vo;
        }).collect(Collectors.toList());

        // 2. 组合
        return ScriptManageUtil.buildCloudBusinessActivityConfigList(voList);
    }

    /**
     * rpcType 判断
     * 非 http, kafka 的直接报错
     *
     * @param businessActivity 业务活动
     * @return 是否支持
     */
    private boolean checkBusinessActivityRpcType(BusinessLinkManageTableEntity businessActivity) {
        String entrance = businessActivity.getEntrace();
        EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(entrance);
        String businessActivityRpcType = entranceJoinEntity.getRpcType();
        // http true
        if (String.valueOf(RpcType.TYPE_WEB_SERVER).equals(businessActivityRpcType)) {
            return true;
        }

        // 如果是 mq 的, 判断是否是 kafka (以后增加支持的配置)
        // 这边可以少差一下 数据库
        if (String.valueOf(RpcType.TYPE_MQ).equals(businessActivityRpcType)) {
            // 查询 link_manage_table, 拿到 features, 判断 是不是 kafka 类型的mq
            String relatedTechLink = businessActivity.getRelatedTechLink();
            ScriptDebugExceptionUtil.isDebugError(StringUtils.isBlank(relatedTechLink), "业务活动关联的技术链路不存在!");
            LinkManageResult linkManageResult = linkManageDAO.selectLinkManageById(Long.valueOf(relatedTechLink));
            ScriptDebugExceptionUtil.isDebugError(linkManageResult == null, "业务活动关联的技术链路不存在!");
            String features = linkManageResult.getFeatures();
            ScriptDebugExceptionUtil.isDebugError(StringUtils.isBlank(features),
                "业务活动关联的技术链路中没有 features 字段, 无法判断业务活动 mq 的类型!");
            LinkManageTableFeaturesVO featureObject = JsonUtil.json2bean(features, LinkManageTableFeaturesVO.class);

            // 配置的支持类型, 是否包含
            List<String> supportRpcTypeList = Arrays.asList(supportRpcType.split(AppConstants.COMMA));
            return supportRpcTypeList.contains(featureObject.getServerMiddlewareType());
        }

        return false;
    }

}
