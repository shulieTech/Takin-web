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

import cn.hutool.core.util.IdUtil;
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
import io.shulie.takin.adapter.api.entrypoint.report.CloudReportApi;
import io.shulie.takin.adapter.api.entrypoint.scenetask.CloudTaskApi;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneTaskStartReq;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.adapter.api.model.request.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.adapter.api.model.request.scenetask.TaskStopReq;
import io.shulie.takin.adapter.api.model.response.report.ReportDetailResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneBusinessActivityRefResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp.SceneSlaRefResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneJobStateResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.checker.CompositeStartConditionChecker;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckResult;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.biz.checker.StartConditionCheckerContext;
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
import io.shulie.takin.web.biz.service.scenemanage.CheckResultVo;
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
 * @author ??????
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

    @Resource
    private CompositeStartConditionChecker startConditionChecker;

    @Resource
    private CloudSceneManageService cloudSceneManageService;
    @Resource
    private PressureTaskDAO pressureTaskDAO;

    /**
     * ?????????????????????
     */
    @Value("${takin.inner.pre:0}")
    private int isInnerPre;

    @Override
    public void preStop(Long sceneId) {
        SceneManageIdReq request = new SceneManageIdReq();
        request.setId(sceneId);
        ResponseResult<?> response = sceneTaskApi.preStopTask(request);
        if (response == null) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, "??????????????????, ?????????!");
        }

        if (!response.getSuccess()) {
            throw ApiException.create(AppConstants.RESPONSE_CODE_FAIL, JsonHelper.bean2Json(response.getError()));
        }

        // ??????????????????
        if (scriptDebugService.checkIsPreStopSuccess(response.getData())) {
            return;
        }
        redisClientUtil.setString(PressureStartCache.getStopTaskMessageKey(sceneId), "????????????", 2, TimeUnit.MINUTES);
        // ?????????????????????
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

        // ???????????????????????????????????????????????????
        LeakVerifyTaskStopRequest stopRequest = new LeakVerifyTaskStopRequest();
        stopRequest.setRefType(VerifyTypeEnum.SCENE.getCode());
        stopRequest.setRefId(sceneId);
        verifyTaskService.stop(stopRequest);
        return responseResult;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @return ????????????
     */
    @Override
    public SceneActionResp startTask(SceneActionParam param) {
        //?????????????????????????????????????????????
        if (applicationService.silenceSwitchStatusIsTrue(WebPluginUtils.traceTenantCommonExt(), AppSwitchEnum.CLOSED)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_VALIDATE_ERROR, "???????????????????????????????????????????????????");
        }

        SceneManageIdReq req = new SceneManageIdReq();
        BeanUtils.copyProperties(param, req);
        req.setId(param.getSceneId());
        ResponseResult<SceneManageWrapperResp> resp = sceneManageApi.getSceneDetail(req);
        if (!resp.getSuccess()) {
            ResponseResult.ErrorInfo errorInfo = resp.getError();
            String errorMsg = Objects.isNull(errorInfo) ? "" : errorInfo.getMsg();
            log.error("takin-cloud?????????????????????????????????id={},???????????????{}", param.getSceneId(), errorMsg);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                getCloudMessage(errorInfo.getCode(), errorInfo.getMsg()));
        }
        String jsonString = JsonHelper.bean2Json(resp.getData());
        SceneManageWrapperDTO sceneData = JsonHelper.json2Bean(jsonString, SceneManageWrapperDTO.class);
        if (null == sceneData) {
            log.error("takin-cloud?????????????????????????????????id={},???????????????{}", param.getSceneId(),
                "sceneData is null! jsonString=" + jsonString);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR,
                "?????????id=" + param.getSceneId() + " ????????????");
        }
        sceneData.setIsAbsoluteScriptPath(FileUtils.isAbsoluteUploadPath(sceneData.getUploadFile(), scriptFilePath));

        if (!SceneManageStatusEnum.RESOURCE_LOCKING.getValue().equals(sceneData.getStatus())) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR,
                "?????????id=" + param.getSceneId() + "?????????????????????????????????????????????");
        }
        // ??????key ???????????????????????????
        redisClientUtil.setString(SceneTaskUtils.getSceneTaskKey(param.getSceneId()),
            DateUtils.getServerTime(),
            Integer.parseInt(sceneData.getPressureTestTime().getTime().toString()), TimeUnit.MINUTES);

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
        //?????????????????????
        UserExt user = WebPluginUtils.traceUser();
        if (user != null) {
            param.setUserId(user.getId());
            param.setUserName(user.getName());
        }

        //???????????????
        if (StringUtils.isEmpty(param.getContinueRead())) {
            param.setContinueRead("-1");
        }
        //???????????????
        SceneActionParamNew paramNew = this.getNewParam(param);
        //????????????
        paramNew.setEnvCode(WebPluginUtils.traceEnvCode());
        paramNew.setTenantId(WebPluginUtils.traceTenantId());

        SceneActionResp startResult;
        try {
            SceneTaskStartReq sceneTaskStartReq = BeanUtil.copyProperties(paramNew, SceneTaskStartReq.class);
            startResult = cloudTaskApi.start(sceneTaskStartReq);
        } catch (Exception e) {
            log.error("takin-cloud?????????????????????????????????id={}", param.getSceneId(), e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
        // ?????? ??????id
        cacheReportId(startResult, param);
        // ?????????
        pushTaskToRedis(startResult, sceneData);

        return startResult;
    }

    private void pushTaskToRedis(SceneActionResp startResult, SceneManageWrapperDTO sceneData) {
        final Long reportId = startResult.getData();
        if (reportId != null) {
            //??????????????????
            if (sceneData.getPressureTestSecond() == null) {
                throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR,
                    "??????????????????????????????????????????" + sceneData.getPressureTestSecond());
            }
            redisClientUtil.setString(PressureStartCache.getReportCachedKey(reportId),
                String.valueOf(System.currentTimeMillis()), 5, TimeUnit.MINUTES);
            cacheReportKey(reportId);
        }
    }

    /**
     * ??????cloud ??????
     *
     * @param code     ????????????
     * @param errorMsg ????????????
     * @return ????????????????????????
     */
    private String getCloudMessage(String code, String errorMsg) {
        return String.format("takin-cloud????????????????????????????????????%s???,???????????????%s???", code, errorMsg);
    }

    private void cacheReportId(SceneActionResp request, SceneActionParam param) {
        if (request == null) {
            log.info("start scene response return data is  illegal??? sceneId:{}", param.getSceneId());
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
            log.error("????????????", e);
        }
        return paramNew;
    }

    /**
     * RedisKey?????????????????????sceneId????????????tenantId:envCode:
     *
     * @param sceneId ????????????
     * @return RedisKey
     */
    private String getCacheReportId(Long sceneId) {
        return PRESSURE_REPORT_ID_SCENE_PREFIX + TenantKeyUtils.getTenantKey() + sceneId;
    }

    @Override
    public ResponseResult<String> stopTask(SceneActionParam param) {
        // ?????? ?????????
        Long sceneId = param.getSceneId();
        redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(sceneId));
        // ??????????????? redis??????
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        WebPluginUtils.fillCloudUserData(req);
        ResponseResult<SceneActionResp> response = sceneTaskApi.checkTask(req);
        if (!response.getSuccess()) {
            throw new TakinWebException(ExceptionCode.SCENE_STOP_ERROR, response.getError());
        }
        SceneActionResp resp = response.getData();
        Long reportId = resp.getReportId();
        String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
            WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
        redisClientUtil.del(redisKey);
        String messageKey = PressureStartCache.getStopTaskMessageKey(sceneId);
        if (!redisClientUtil.hasKey(messageKey)) {
            redisClientUtil.setString(messageKey, "??????????????????", 2, TimeUnit.MINUTES);
        }
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
        //????????????
        if (response.getData() == null || resp.getData() != 2L) {
            // ?????? ?????????
            redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(sceneId));
            return response;
        }
        Long reportId = resp.getReportId();

        /**
         * ??????????????????????????????cpu??????????????????????????????
         * !report.getPressureType().equals(0) ??????????????? ??????cpu?????????????????????????????????
         */
        final ReportDetailByIdReq idReq = new ReportDetailByIdReq();
        idReq.setReportId(reportId);
        WebPluginUtils.fillCloudUserData(idReq);
        final ReportDetailResp report = cloudReportApi.getReportByReportId(idReq);
        if (null != report && !report.getPressureType().equals(0)) {
            return response;
        }

        //????????????SLA ?????? | cpu
        ResponseResult<SceneManageWrapperResp> detailResp = sceneManageApi.getSceneDetail(req);
        if (!detailResp.getSuccess()) {
            return response;
        }
        SceneManageWrapperResp wrapperResp = JSONObject.parseObject(JSON.toJSONString(detailResp.getData()),
            SceneManageWrapperResp.class);
        // ????????????????????????
        List<SceneBusinessActivityRefResp> sceneBusinessActivityRefList = wrapperResp.getBusinessActivityConfig();
        //????????????????????????ID???????????????ID
        List<Long> applicationIds = sceneBusinessActivityRefList.stream()
            .map(SceneBusinessActivityRefResp::getApplicationIds)
            .filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(",")).map(Long::parseLong))
            .filter(data -> data > 0L).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(applicationIds)) {
            List<ApplicationDetailResult> applicationMntList = applicationDAO.getApplicationByIds(applicationIds);
            List<String> applicationNames = applicationMntList.stream().map(ApplicationDetailResult::getApplicationName)
                .collect(Collectors.toList());
            // ????????????????????? ???????????? + 10s
            String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
                WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
                String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
            redisClientUtil.set(redisKey, applicationNames, wrapperResp.getPressureTestSecond() + 10);
        }

        Map<String, List<SceneSlaRefResp>> slaMap = getSceneSla(wrapperResp);
        if (MapUtils.isNotEmpty(slaMap)) {
            //??????????????????
            List<String> appNames = reportApplicationService.getReportApplication(reportId).getApplicationNames();
            // ???????????????????????????cpu???memory
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
            log.error("??????TPS??????", e);
            throw new RuntimeException("??????TPS??????", e);
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
            log.error("????????????TPS??????", e);
            return null;
        }
    }

    @Override
    public List<String> checkBusinessActivitiesConfig(List<Long> businessActivityIds) {
        // ?????????????????????????????????????????????
        LeakSqlBatchRefsRequest refsRequest = new LeakSqlBatchRefsRequest();
        refsRequest.setBusinessActivityIds(businessActivityIds);
        List<VerifyTaskConfig> verifyTaskConfigList = leakSqlService.getVerifyTaskConfig(refsRequest);

        if (CollectionUtils.isEmpty(verifyTaskConfigList)) {
            return Collections.singletonList("?????????????????????????????????????????????");
        }

        // ????????????????????????????????????
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

        // ??????sql?????????????????????
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
        //??????????????????????????????????????????????????????????????????
        String switchStatus = applicationService.getUserSwitchStatusForVo();
        if (!AppSwitchEnum.OPENED.getCode().equals(switchStatus)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_START_STATUS_ERROR, "?????????????????????????????????????????????");
        }
    }

    /**
     * ??????????????????
     *
     * @param sceneData ????????????
     */
    @Override
    public String checkScriptCorrelation(SceneManageWrapperDTO sceneData) {
        ScriptCheckDTO scriptCheck = sceneManageService.checkBusinessActivityAndScript(sceneData);
        return scriptCheck.getErrmsg();
    }

    /**
     * ??????????????????
     *
     * @param applicationMntList ????????????
     * @return ????????????
     */
    @Override
    public String checkApplicationCorrelation(List<ApplicationDetailResult> applicationMntList) {
        // ????????? ?????????????????? ??????????????? ?????????????????????
        List<String> appNames = applicationMntList.stream().map(ApplicationDetailResult::getApplicationName).collect(
            Collectors.toList());
        // ??????????????????????????? todo ?????????????????????????????????????????????????????????????????????
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(appNames);
        Map<String, List<ApplicationResult>> listMap = applicationResultList.stream().collect(
            Collectors.groupingBy(ApplicationResult::getAppName));

        List<String> applicationNameList = applicationMntList.stream().map(application -> {
            boolean statusError = false;

            if (!AppSwitchEnum.OPENED.getCode().equals(application.getSwitchStatus())) {
                log.error("??????[{}]?????????", application.getApplicationName());
                statusError = true;
            }

            if (!AppAccessTypeEnum.NORMAL.getValue().equals(application.getAccessStatus())) {
                log.error("??????[{}]?????????????????????????????????????????????[{}]", application.getApplicationName(),
                    application.getAccessStatus());
                statusError = true;
            }
            //???????????????????????????
            Integer totalNodeCount = application.getNodeNum();
            List<ApplicationResult> results = listMap.get(application.getApplicationName());
            if (CollectionUtils.isEmpty(results) || !totalNodeCount.equals(results.get(0).getInstanceInfo()
                .getInstanceOnlineAmount())) {
                log.error("??????[{}]???????????????????????????????????????", application.getApplicationName());
                statusError = true;
            }

            return statusError ? application.getApplicationName() : "";
        }).filter(StrUtil::isNotBlank).collect(Collectors.toList());

        if (applicationNameList.isEmpty()) {
            return "";
        }

        return String.format("%d???????????????????????????%s", applicationNameList.size(),
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
     * ????????????
     *
     * @param sceneId                      ??????id
     * @param sceneBusinessActivityRefList ??????????????????????????????
     * @return ??????ids
     */
    private List<Long> listApplicationIdsFromScene(Long sceneId,
        List<SceneBusinessActivityRefDTO> sceneBusinessActivityRefList) {
        // ????????????????????????ID???????????????ID
        List<Long> applicationIds = sceneBusinessActivityRefList.stream()
            .map(SceneBusinessActivityRefDTO::getApplicationIds).filter(StringUtils::isNotEmpty)
            .flatMap(appIds -> Arrays.stream(appIds.split(","))
                .map(Long::valueOf)).filter(data -> data > 0L).distinct().collect(Collectors.toList());
        if (applicationIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ????????????????????????id
        List<Long> excludedApplicationIds = sceneExcludedApplicationDAO.listApplicationIdsBySceneId(sceneId);
        if (excludedApplicationIds.isEmpty()) {
            return applicationIds;
        }

        // ??????????????????id
        applicationIds.removeAll(excludedApplicationIds);
        return applicationIds;
    }

    @Override
    public CheckResultVo preCheck(SceneActionParam param) {
        Long sceneId = param.getSceneId();
        String resourceId = param.getResourceId();
        StartConditionCheckerContext context = new StartConditionCheckerContext();
        context.setUniqueKey(IdUtil.fastSimpleUUID());
        context.setSceneId(sceneId);
        context.setResourceId(resourceId);
        context.setTenantId(WebPluginUtils.traceTenantId());

        SceneManageQueryOptions options = new SceneManageQueryOptions();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        SceneManageWrapperOutput sceneManage = cloudSceneManageService.getSceneManage(sceneId, options);
        context.setSceneData(sceneManage);

        List<CheckResult> webResultList = startConditionChecker.doCheck(context);
        boolean existsFail = false;
        boolean existsPending = false;
        Long reportId = context.getReportId();
        boolean notExistsResourceId = StringUtils.isBlank(resourceId);
        String resource = resourceId;
        for (CheckResult result : webResultList) {
            if (result.getStatus().equals(CheckStatus.FAIL.ordinal())) {
                existsFail = true;
            } else if (result.getStatus().equals(CheckStatus.PENDING.ordinal())) {
                existsPending = true;
            }
            String tmpResourceId;
            if (notExistsResourceId && StringUtils.isNotBlank(tmpResourceId = result.getResourceId())) {
                resource = tmpResourceId;
            }
        }
        if (existsFail) {
            return CheckResultVo.builder().sceneName(sceneManage.getPressureTestSceneName())
                .podNumber(sceneManage.getIpNum()).status(CheckStatus.FAIL.ordinal())
                .reportId(reportId).checkList(webResultList).build();
        }
        int status = existsPending ? CheckStatus.PENDING.ordinal() : CheckStatus.SUCCESS.ordinal();
        return CheckResultVo.builder().sceneName(sceneManage.getPressureTestSceneName())
            .podNumber(sceneManage.getIpNum()).status(status).reportId(reportId)
            .resourceId(resource).checkList(webResultList).build();
    }

    @Override
    public void cacheReportKey(Long reportId) {
        //???????????? 2??????
        final LocalDateTime dateTime = LocalDateTime.now().plusHours(2);
        //??????
        SceneTaskDto taskDto = new SceneTaskDto(reportId, ContextSourceEnum.JOB_SCENE, dateTime);
        //???????????????redis??????
        final String reportKeyName = isInnerPre == 1 ? WebRedisKeyConstant.SCENE_REPORTID_KEY_FOR_INNER_PRE
            : WebRedisKeyConstant.SCENE_REPORTID_KEY;
        final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
        redisTemplate.opsForList().leftPush(reportKeyName, reportKey);
        redisTemplate.opsForValue().set(reportKey, JSON.toJSONString(taskDto));
    }

    @Override
    public void forceStop(String resourceId) {
        ReportDetailResp report = cloudReportApi.getReportByResourceId(resourceId);
        if (Objects.nonNull(report)) {
            if (report.getTaskStatus() == ReportConstants.FINISH_STATUS) {
                pressureTaskDAO.updateStatus(report.getTaskId(), PressureTaskStateEnum.REPORT_DONE, "????????????");
                return;
            }
            TaskStopReq req = new TaskStopReq();
            req.setReportId(report.getId());
            req.setFinishReport(false);
            cloudTaskApi.forceStopInspectTask(req);
        }
    }
}
