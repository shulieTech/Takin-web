package io.shulie.takin.web.biz.service.scenemanage.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.AppAccessTypeEnum;
import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.constant.VerifyTypeEnum;
import com.pamirs.takin.common.exception.ApiException;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneBusinessActivityRefDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParamNew;
import com.pamirs.takin.entity.domain.vo.report.ScenePluginParam;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.cloud.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.cloud.sdk.model.request.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp.SceneBusinessActivityRefResp;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceTestRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.SqlTestRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskStopRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.VerifyTaskConfig;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.UpdateTpsRequest;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.DataSourceService;
import io.shulie.takin.web.biz.service.LeakSqlService;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.biz.service.async.AsyncService;
import io.shulie.takin.web.biz.service.report.impl.ReportApplicationService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.FileUtils;
import io.shulie.takin.web.biz.utils.TenantKeyUtils;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.common.vo.scene.BaffleAppVO;
import io.shulie.takin.web.data.dao.SceneExcludedApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 莫问
 * @date 2020-04-22
 */
@Slf4j
@Service
public class SceneTaskServiceImpl implements SceneTaskService {

    public static final String PRESSURE_REPORT_ID_SCENE_PREFIX = "pressure:reportId:scene:";

    @Value("${file.upload.script.path:/nfs/takin/script/}")
    private String scriptFilePath;

    @Resource
    private LeakSqlService leakSqlService;
    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private SceneManageService sceneManageService;
    @Resource
    private SceneTaskApi sceneTaskApi;
    @Resource
    private SceneManageApi sceneManageApi;
    @Resource
    private ReportApplicationService reportApplicationService;
    @Resource
    private AsyncService asyncService;
    @Resource
    private ApplicationService applicationService;
    @Resource
    private CloudTaskApi cloudTaskApi;
    @Resource
    private ScriptManageService scriptManageService;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private BaseConfigService baseConfigService;

    @Autowired
    private ScriptDebugService scriptDebugService;

    @Autowired
    private VerifyTaskService verifyTaskService;

    @Autowired
    private SceneExcludedApplicationDAO sceneExcludedApplicationDAO;

    @Resource
    private CloudReportApi cloudReportApi;

    /**
     * 是否是预发环境
     */
    @Value("${takin.inner.pre:0}")
    private int isInnerPre;

    @Override
    public void preStop(Long sceneId) {
        SceneManageIdReq request = new SceneManageIdReq();
        request.setId(sceneId);
        ResponseResult<?> response = sceneTaskApi.preStopTask(request);
        if (response == null) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "停止压测失败, 请重试!");
        }

        if (!response.getSuccess()) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, JsonHelper.bean2Json(response.getError()));
        }

        // 提前停止成功
        if (scriptDebugService.checkIsPreStopSuccess(response.getData())) {
            return;
        }

        // 不成功调用停止
        this.stop(sceneId);
    }

    @Override
    public ResponseResult<String> stop(Long sceneId) {
        SceneActionParam sceneActionParam = new SceneActionParam();
        sceneActionParam.setSceneId(sceneId);
        ResponseResult<String> responseResult = this.stopTask(sceneActionParam);
        if (responseResult == null || !responseResult.getSuccess()) {
            return responseResult;
        }

        // 如果有验证任务，则同时停止验证任务
        LeakVerifyTaskStopRequest stopRequest = new LeakVerifyTaskStopRequest();
        stopRequest.setRefType(VerifyTypeEnum.SCENE.getCode());
        stopRequest.setRefId(sceneId);
        verifyTaskService.stop(stopRequest);
        return responseResult;
    }

    /**
     * 查询场景业务活动信息，校验业务活动
     *
     * @return 启动结果
     */
    @Override
    public SceneActionResp startTask(SceneActionParam param) {
        //探针总开关关闭状态禁止启动压测
        if (applicationService.silenceSwitchStatusIsTrue(WebPluginUtils.traceTenantCommonExt(), AppSwitchEnum.CLOSED)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, "启动压测场景失败，探针总开关已关闭");
        }

        SceneManageIdReq req = new SceneManageIdReq();
        BeanUtils.copyProperties(param, req);
        req.setId(param.getSceneId());
        ResponseResult<SceneManageWrapperResp> resp = sceneManageApi.getSceneDetail(req);
        if (!resp.getSuccess()) {
            ResponseResult.ErrorInfo errorInfo = resp.getError();
            String errorMsg = Objects.isNull(errorInfo) ? "" : errorInfo.getMsg();
            log.error("takin-cloud查询场景信息返回错误，id={},错误信息：{}", param.getSceneId(), errorMsg);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                getCloudMessage(errorInfo.getCode(), errorInfo.getMsg()));
        }
        String jsonString = JsonHelper.bean2Json(resp.getData());
        SceneManageWrapperDTO sceneData = JsonHelper.json2Bean(jsonString, SceneManageWrapperDTO.class);
        if (null == sceneData) {
            log.error("takin-cloud查询场景信息返回错误，id={},错误信息：{}", param.getSceneId(),
                "sceneData is null! jsonString=" + jsonString);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                "场景，id=" + param.getSceneId() + " 信息为空");
        }
        sceneData.setIsAbsoluteScriptPath(FileUtils.isAbsoluteUploadPath(sceneData.getUploadFile(), scriptFilePath));

        // 校验该场景是否正在压测中
        if (!SceneManageStatusEnum.ifFinished(sceneData.getStatus())) {
            //        if (redisClientUtil.hasKey(SceneTaskUtils.getSceneTaskKey(param.getSceneId()))) {
            // 正在压测中
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR,
                "场景，id=" + param.getSceneId() + "已启动压测，请刷新页面！");
        } else {
            // 记录key 过期时长为压测时长
            redisClientUtil.setString(SceneTaskUtils.getSceneTaskKey(param.getSceneId()),
                DateUtils.getServerTime(),
                Integer.parseInt(sceneData.getPressureTestTime().getTime().toString()), TimeUnit.MINUTES);
        }

        preCheckStart(sceneData);

        if (sceneData != null && sceneData.getScriptId() != null) {
            ScriptManageDeployDetailResponse scriptManageDeployDetail = scriptManageService.getScriptManageDeployDetail(
                sceneData.getScriptId());
            List<PluginConfigDetailResponse> pluginConfigDetailResponseList = scriptManageDeployDetail
                .getPluginConfigDetailResponseList();
            if (CollectionUtils.isNotEmpty(pluginConfigDetailResponseList)) {
                List<Long> pluginIds = pluginConfigDetailResponseList.stream().map(o -> Long.parseLong(o.getName()))
                    .collect(Collectors.toList());
                param.setEnginePluginIds(pluginIds);
                param.setEnginePlugins(pluginConfigDetailResponseList.stream()
                    .map(detail -> new ScenePluginParam() {{
                        setPluginId(Long.parseLong(detail.getName()));
                        setPluginVersion(detail.getVersion());
                    }}).collect(Collectors.toList()));
            }
        }
        //填充操作人信息
        UserExt user = WebPluginUtils.traceUser();
        if (user != null) {
            param.setUserId(user.getId());
            param.setUserName(user.getName());
        }

        //兼容老版本
        if (StringUtils.isEmpty(param.getContinueRead())) {
            param.setContinueRead("-1");
        }
        //新版本位点
        SceneActionParamNew paramNew = this.getNewParam(param);
        //设置租户
        paramNew.setEnvCode(WebPluginUtils.traceEnvCode());
        paramNew.setTenantId(WebPluginUtils.traceTenantId());

        SceneActionResp startResult;
        try {
            SceneTaskStartReq sceneTaskStartReq = BeanUtil.copyProperties(paramNew, SceneTaskStartReq.class);
            startResult = cloudTaskApi.start(sceneTaskStartReq);
        } catch (Exception e) {
            log.error("takin-cloud启动压测场景返回错误，id={}", param.getSceneId(), e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
        // 缓存 报告id
        cacheReportId(startResult, param);
        // 入队列
        pushTaskToRedis(startResult, sceneData);

        return startResult;
    }

    private void pushTaskToRedis(SceneActionResp startResult, SceneManageWrapperDTO sceneData) {
        final Long reportId = startResult.getData();
        if (reportId != null) {
            //计算结束时间
            if (sceneData.getPressureTestSecond() == null) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR,
                    "获取压测时长失败！压测时长为" + sceneData.getPressureTestSecond());
            }
            //兜底时长 2小时
            final LocalDateTime dateTime = LocalDateTime.now().plusHours(2);
            //组装
            SceneTaskDto taskDto = new SceneTaskDto(reportId, ContextSourceEnum.JOB_SCENE, dateTime);
            //任务添加到redis队列
            final String reportKeyName = isInnerPre == 1 ? WebRedisKeyConstant.SCENE_REPORTID_KEY_FOR_INNER_PRE
                : WebRedisKeyConstant.SCENE_REPORTID_KEY;
            final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
            redisTemplate.opsForList().leftPush(reportKeyName, reportKey);
            redisTemplate.opsForValue().set(reportKey, JSON.toJSONString(taskDto));
        }
    }

    /**
     * 返回cloud 数据
     *
     * @param code     错误编码
     * @param errorMsg 错误信息
     * @return 拼接后的错误信息
     */
    private String getCloudMessage(String code, String errorMsg) {
        return String.format("takin-cloud启动场景失败，异常代码【%s】,异常原因【%s】", code, errorMsg);
    }

    private void cacheReportId(SceneActionResp request, SceneActionParam param) {
        if (request == null) {
            log.info("start scene response return data is  illegal！ sceneId:{}", param.getSceneId());
            return;
        }
        Long reportId = request.getData();
        redisTemplate.opsForValue().set(getCacheReportId(param.getSceneId()), reportId, 1L, TimeUnit.DAYS);
    }

    private SceneActionParamNew getNewParam(SceneActionParam param) {
        SceneActionParamNew paramNew = BeanUtil.copyProperties(param, SceneActionParamNew.class);
        try {
            paramNew.setContinueRead("1".equals(param.getContinueRead()));
        } catch (Exception e) {
            log.error("未知异常", e);
        }
        return paramNew;
    }

    /**
     * RedisKey改造，在原有的sceneId前面追加tenantId:envCode:
     *
     * @param sceneId 场景主键
     * @return RedisKey
     */
    private String getCacheReportId(Long sceneId) {
        return PRESSURE_REPORT_ID_SCENE_PREFIX + TenantKeyUtils.getTenantKey() + sceneId;
    }

    @Override
    public ResponseResult<String> stopTask(SceneActionParam param) {
        // 释放 场景锁
        redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(param.getSceneId()));
        // 停止先删除 redis中的
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(param.getSceneId());
        WebPluginUtils.fillCloudUserData(req);
        ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_STOP_ERROR, response.getError());
        }
        SceneActionResp resp = response.getData();
        String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
            WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, resp.getReportId()));
        redisClientUtil.del(redisKey);
        // 最后删除
        return sceneTaskApi.stopTask(req);
    }

    @Override
    public ResponseResult<SceneActionResp> checkStatus(Long sceneId, Long frontReportId) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        req.setReportId(frontReportId);
        WebPluginUtils.fillCloudUserData(req);
        ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, response.getError());
        }
        SceneActionResp resp = response.getData();
        //非压测中
        if (response.getData() == null || resp.getData() != 2L) {
            // 解除 场景锁
            redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(sceneId));
            return response;
        }
        Long reportId = resp.getReportId();

        /**
         * 只有普通报告需要获取cpu内存等数据和做监控！
         * !report.getPressureType().equals(0) 表示不需要 获取cpu内存等数据和监控等操作
         */
        final ReportDetailByIdReq idReq = new ReportDetailByIdReq();
        idReq.setReportId(reportId);
        WebPluginUtils.fillCloudUserData(idReq);
        final ReportDetailResp report = cloudReportApi.getReportByReportId(idReq);
        if (null != report && !report.getPressureType().equals(0)) {
            return response;
        }

        //获取场景SLA 内存 | cpu
        ResponseResult<SceneManageWrapperResp> detailResp = sceneManageApi.getSceneDetail(req);
        if (!detailResp.getSuccess()) {
            return response;
        }
        SceneManageWrapperResp wrapperResp = JSONObject.parseObject(JSON.toJSONString(detailResp.getData()),
            SceneManageWrapperResp.class);
        // 缓存压测中的应用
        List<SceneBusinessActivityRefResp> sceneBusinessActivityRefList = wrapperResp.getBusinessActivityConfig();
        //从活动中提取应用ID，去除重复ID
        List<Long> applicationIds = sceneBusinessActivityRefList.stream()
            .map(SceneBusinessActivityRefResp::getApplicationIds)
            .filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(",")).map(Long::parseLong))
            .filter(data -> data > 0L).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(applicationIds)) {
            List<ApplicationDetailResult> applicationMntList = applicationDAO.getApplicationByIds(applicationIds);
            List<String> applicationNames = applicationMntList.stream().map(ApplicationDetailResult::getApplicationName)
                .collect(Collectors.toList());
            // 过期时间，根据 压测时间 + 10s
            String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
                WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
                String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
            redisClientUtil.set(redisKey, applicationNames, wrapperResp.getPressureTestSecond() + 10);
        }

        Map<String, List<SceneSlaRefResp>> slaMap = getSceneSla(wrapperResp);
        if (MapUtils.isNotEmpty(slaMap)) {
            //获取应用列表
            List<String> appNames = reportApplicationService.getReportApplication(reportId).getApplicationNames();
            // 创建监控线程，监控cpu及memory
            if (CollectionUtils.isNotEmpty(appNames)) {
                asyncService.monitorCpuMemory(sceneId, reportId, appNames, slaMap.get("stop"), slaMap.get("warn"));
            }
        }
        return response;
    }

    @Override
    public void updateTaskTps(UpdateTpsRequest request) {
        SceneTaskUpdateTpsReq req = new SceneTaskUpdateTpsReq();
        req.setSceneId(request.getSceneId());
        req.setReportId(request.getReportId());
        req.setTpsNum(request.getTargetTps());
        req.setXpathMd5(request.getXpathMd5());
        try {
            cloudTaskApi.updateSceneTaskTps(req);
        } catch (Throwable e) {
            log.error("修改TPS失败", e);
            throw new RuntimeException("修改TPS失败", e);
        }
    }

    @Override
    public Long queryTaskTps(Long reportId, Long sceneId, String xPathMd5) {
        SceneTaskQueryTpsReq req = new SceneTaskQueryTpsReq();
        req.setSceneId(sceneId);
        req.setReportId(reportId);
        req.setXpathMd5(xPathMd5);
        try {
            SceneTaskAdjustTpsResp sceneTaskAdjustTpsResp = cloudTaskApi.queryAdjustTaskTps(req);
            return sceneTaskAdjustTpsResp.getTotalTps();
        } catch (Throwable e) {
            log.error("查询任务TPS失败", e);
            return null;
        }
    }

    @Override
    public List<String> checkBusinessActivitiesConfig(List<Long> businessActivityIds) {
        // 校验压测场景下是否存在漏数配置
        LeakSqlBatchRefsRequest refsRequest = new LeakSqlBatchRefsRequest();
        refsRequest.setBusinessActivityIds(businessActivityIds);
        List<VerifyTaskConfig> verifyTaskConfigList = leakSqlService.getVerifyTaskConfig(refsRequest);

        if (CollectionUtils.isEmpty(verifyTaskConfigList)) {
            return Collections.singletonList("关联的业务活动暂未配置漏数脚本");
        }

        // 校验数据源是否能正常连接
        List<String> errorDbMsgList = verifyTaskConfigList.stream()
            .map(verifyTaskConfig -> {
                DataSourceTestRequest testRequest = new DataSourceTestRequest();
                testRequest.setJdbcUrl(verifyTaskConfig.getJdbcUrl());
                testRequest.setUsername(verifyTaskConfig.getUsername());
                testRequest.setPassword(verifyTaskConfig.getPassword());
                testRequest.setType(verifyTaskConfig.getType());
                return dataSourceService.testConnection(testRequest);
            })
            .filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(errorDbMsgList)) {
            return errorDbMsgList.stream().sorted().collect(Collectors.toList());
        }

        // 校验sql是否能正常运行
        List<String> errorSqlMsgList = verifyTaskConfigList.stream()
            .map(verifyTaskConfig -> {
                SqlTestRequest testRequest = new SqlTestRequest();
                testRequest.setDatasourceId(verifyTaskConfig.getDatasourceId());
                testRequest.setSqls(verifyTaskConfig.getSqls());
                return leakSqlService.testSqlConnection(testRequest);
            }).filter(StringUtils::isNotBlank).map(msg -> {
                if (msg.contains(AppConstants.COMMA)) {
                    return Arrays.asList(msg.split(AppConstants.COMMA));
                } else {
                    return Collections.singletonList(msg);
                }
            }).flatMap(Collection::stream).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(errorSqlMsgList)) {
            return errorSqlMsgList.stream().sorted().collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void preCheckStart(SceneManageWrapperDTO sceneData) {
        //检查压测开关，压测开关处于关闭状态时禁止压测
        String userSwitchStatusForVo = applicationService.getUserSwitchStatusForVo();
        if (StringUtils.isBlank(userSwitchStatusForVo) || !userSwitchStatusForVo.equals(
            AppSwitchEnum.OPENED.getCode())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR, "压测开关处于关闭状态，禁止压测");
        }

        //检查场景是否存可以开启启压测
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefList = sceneData.getBusinessActivityConfig();
        if (CollectionUtils.isEmpty(sceneBusinessActivityRefList)) {
            log.error("[{}]场景没有配置业务活动", sceneData.getId());
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR,
                "启动压测失败，没有配置业务活动，场景ID为" + sceneData.getId());
        }

        // 业务活动相关检查
        this.checkBusinessActivity(sceneData, sceneBusinessActivityRefList);
    }

    /**
     * 检查业务活动相关
     *
     * @param sceneData                    入参
     * @param sceneBusinessActivityRefList 业务活动
     */
    private void checkBusinessActivity(SceneManageWrapperDTO sceneData,
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefList) {
        //需求要求，业务验证异常需要详细输出
        StringBuilder errorMsg = new StringBuilder();

        // 获得场景关联的排除应用ids
        List<Long> applicationIds = this.listApplicationIdsFromScene(sceneData.getId(), sceneBusinessActivityRefList);

        // 应用相关检查
        boolean checkApplication = ConfigServerHelper.getBooleanValueByKey(
            ConfigServerKeyEnum.TAKIN_START_TASK_CHECK_APPLICATION);
        if (!CollectionUtils.isEmpty(applicationIds) && checkApplication) {
            List<ApplicationDetailResult> applicationMntList = applicationDAO.getApplicationByIds(applicationIds);
            // todo 临时方案，过滤挡板应用
            TBaseConfig config = baseConfigService.queryByConfigCode(ConfigConstants.SCENE_BAFFLE_APP_CONFIG);
            if (config != null && StringUtils.isNotBlank(config.getConfigValue())) {
                try {
                    List<BaffleAppVO> baffleAppVos = JsonHelper.json2List(config.getConfigValue(), BaffleAppVO.class);
                    List<String> appNames = Optional
                        .of(baffleAppVos.stream()
                            .filter(appVO -> sceneData.getId() != null && sceneData.getId().equals(appVO.getSceneId()))
                            .collect(Collectors.toList()))
                        .map(t -> t.get(0)).map(BaffleAppVO::getAppName).orElse(Lists.newArrayList());
                    List<Long> appIds = Lists.newArrayList();

                    List<ApplicationDetailResult> tempApps = applicationMntList.stream().filter(app -> {
                        if (appNames.contains(app.getApplicationName())) {
                            // 用于过滤应用id
                            appIds.add(app.getApplicationId());
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Long> tempAppIds = applicationIds.stream().filter(id -> !appIds.contains(id)).collect(
                        Collectors.toList());
                    applicationMntList = tempApps;
                    applicationIds = tempAppIds;
                } catch (Exception e) {
                    log.error("场景挡板配置转化异常：配置项：{},配置项内容:{}", ConfigConstants.SCENE_BAFFLE_APP_CONFIG,
                        config.getConfigValue());
                }
            }
            if (CollectionUtils.isEmpty(applicationMntList) || applicationMntList.size() != applicationIds.size()) {
                log.error("启动压测失败, 没有找到关联的应用信息，场景ID：{}", sceneData.getId());
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR,
                    "启动压测失败, 没有找到关联的应用信息，场景ID：" + sceneData.getId());
            }

            // 检查应用相关
            errorMsg.append(this.checkApplicationCorrelation(applicationMntList));
        }

        // 压测脚本文件检查
        String scriptCorrelation = this.checkScriptCorrelation(sceneData);
        errorMsg.append(scriptCorrelation == null ? "" : scriptCorrelation);
        if (errorMsg.length() > 0) {
            String msg;
            if (errorMsg.toString().endsWith(Constants.SPLIT)) {
                msg = StringUtils.substring(errorMsg.toString(), 0, errorMsg.toString().length() - 1);
            } else {
                msg = errorMsg.toString();
            }
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, msg);
        }
    }

    /**
     * 脚本相关检查
     *
     * @param sceneData 所需参数
     */
    @Override
    public String checkScriptCorrelation(SceneManageWrapperDTO sceneData) {
        ScriptCheckDTO scriptCheck = sceneManageService.checkBusinessActivityAndScript(sceneData);
        return scriptCheck.getErrmsg();
    }

    /**
     * 应用相关检查
     *
     * @param applicationMntList 应用列表
     * @return 错误信息
     */
    @Override
    public String checkApplicationCorrelation(List<ApplicationDetailResult> applicationMntList) {
        // 查询下 应用节点信息 节点不一致 也需要返回异常
        List<String> appNames = applicationMntList.stream().map(ApplicationDetailResult::getApplicationName).collect(
            Collectors.toList());
        // 从大数据里查出数据 todo 目前大数据不区分客户，所以有可能存在不准确问题
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNames);
        Map<String, List<ApplicationResult>> listMap = applicationResultList.stream().collect(
            Collectors.groupingBy(ApplicationResult::getAppName));

        List<String> applicationNameList = applicationMntList.stream().map(application -> {
            boolean statusError = false;

            if (!AppSwitchEnum.OPENED.getCode().equals(application.getSwitchStatus())) {
                log.error("应用[{}]未开启", application.getApplicationName());
                statusError = true;
            }

            if (!AppAccessTypeEnum.NORMAL.getValue().equals(application.getAccessStatus())) {
                log.error("应用[{}]接入状态不是开启状态，当前状态[{}]", application.getApplicationName(),
                    application.getAccessStatus());
                statusError = true;
            }
            //判断节点数是否异常
            Integer totalNodeCount = application.getNodeNum();
            List<ApplicationResult> results = listMap.get(application.getApplicationName());
            if (CollectionUtils.isEmpty(results) || !totalNodeCount.equals(results.get(0).getInstanceInfo()
                .getInstanceOnlineAmount())) {
                log.error("应用[{}]在线节点数与节点总数不一致", application.getApplicationName());
                statusError = true;
            }

            return statusError ? application.getApplicationName() : "";
        }).filter(StrUtil::isNotBlank).collect(Collectors.toList());

        if (applicationNameList.isEmpty()) {
            return "";
        }

        return String.format("%d个应用状态不正常：%s", applicationNameList.size(),
            StringUtils.join(applicationNameList.toArray(), AppConstants.COMMA)) + Constants.SPLIT;
    }

    @Override
    public Long getReportIdFromCache(Long sceneId) {
        String cacheReportId = getCacheReportId(sceneId);
        Object reportId = redisTemplate.opsForValue().get(cacheReportId);
        if (reportId == null) {
            return null;
        }
        return Long.valueOf((Integer)reportId);
    }

    @Override
    public ResponseResult<SceneJobStateResp> checkSceneJobStatus(Long sceneId) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        ResponseResult<SceneJobStateResp> response = sceneTaskApi.checkJobStateStatus(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_CHECK_ERROR, response.getError());
        }

        return response;
    }

    private Map<String, List<SceneSlaRefResp>> getSceneSla(SceneManageWrapperResp detailResp) {
        Map<String, List<SceneSlaRefResp>> dataMap = Maps.newHashMap();
        List<SceneSlaRefResp> stopList = Optional.ofNullable(detailResp.getStopCondition()).orElse(
            Lists.newArrayList());
        stopList = stopList.stream().filter(data -> data.getRule().getIndexInfo() >= 4).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(stopList)) {
            dataMap.put("stop", stopList);
        }
        List<SceneSlaRefResp> warnList = Optional.ofNullable(detailResp.getWarningCondition()).orElse(
            Lists.newArrayList());
        warnList = warnList.stream().filter(data -> data.getRule().getIndexInfo() >= 4).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(warnList)) {
            dataMap.put("warn", warnList);
        }
        return dataMap;
    }

    /**
     * 获得场景
     *
     * @param sceneId                      场景id
     * @param sceneBusinessActivityRefList 场景关联业务活动列表
     * @return 应用ids
     */
    private List<Long> listApplicationIdsFromScene(Long sceneId,
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefList) {
        // 从活动中提取应用ID，去除重复ID
        List<Long> applicationIds = sceneBusinessActivityRefList.stream()
            .map(SceneBusinessActivityRefDTO::getApplicationIds).filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(","))
                .map(Long::valueOf)).filter(data -> data > 0L).distinct().collect(Collectors.toList());
        if (applicationIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 场景对应的排除的id
        List<Long> excludedApplicationIds = sceneExcludedApplicationDAO.listApplicationIdsBySceneId(sceneId);
        if (excludedApplicationIds.isEmpty()) {
            return applicationIds;
        }

        // 排除掉排除的id
        applicationIds.removeAll(excludedApplicationIds);
        return applicationIds;
    }

}
