package io.shulie.takin.cloud.biz.service.scenetask;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.domain.entity.report.Report;
import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneFileReadPosition;
import com.pamirs.takin.cloud.entity.domain.vo.file.FileSliceRequest;
import com.pamirs.takin.cloud.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.model.common.RuleBean;
import io.shulie.takin.adapter.api.model.common.TimeBean;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStopReq;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.biz.cache.SceneTaskStatusCache;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.input.scenemanage.EnginePluginInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneBusinessActivityRefInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneInspectInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskQueryTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartCheckInput.FileInfo;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskUpdateTpsInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTryRunInput;
import io.shulie.takin.cloud.biz.notify.StartFailEventSource;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStartOutput;
import io.shulie.takin.cloud.biz.output.report.SceneInspectTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneScriptRefOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneActionOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneJobStateOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneRunTaskStatusOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStartCheckOutput.FileReadInfo;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTaskStopOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStartOutput;
import io.shulie.takin.cloud.biz.output.scenetask.SceneTryRunTaskStatusOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskEventService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.constants.SceneStartCheckConstants;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureModeEnum;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.common.enums.ThreadGroupTypeEnum;
import io.shulie.takin.cloud.common.enums.TimeUnitEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneStopReasonEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.StartEndPair;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportBusinessActivityDetailDao;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskVarietyDAO;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskVarietyEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageListResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.content.asset.AccountInfoExt;
import io.shulie.takin.cloud.ext.content.asset.AssetBalanceExt;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.asset.AssetInvoiceExt;
import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.security.MD5Utils;
import io.shulie.takin.web.biz.checker.EngineResourceChecker;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckResult;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.biz.checker.StartConditionCheckerContext;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ??????
 * @date 2020-04-22
 */
@Service
@Slf4j
public class CloudSceneTaskServiceImpl extends AbstractIndicators implements CloudSceneTaskService {
    @Resource
    private ReportDao reportDao;
    @Resource
    private ReportMapper reportMapper;
    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private SceneManageDAO sceneManageDao;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private FileSliceService fileSliceService;
    @Resource
    private DynamicTpsService dynamicTpsService;
    @Resource
    private SceneTaskStatusCache taskStatusCache;
    @Resource
    private CloudSceneManageService cloudSceneManageService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SceneTaskEventService sceneTaskEventService;
    @Resource
    private EngineResourceChecker engineResourceChecker;
    @Resource
    private PressureTaskDAO pressureTaskDAO;
    @Resource
    private ReportBusinessActivityDetailDao reportBusinessActivityDetailDao;
    @Resource
    private EventCenterTemplate eventCenterTemplate;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private PressureTaskVarietyDAO pressureTaskVarietyDAO;
    @Resource
    private PressureTaskApi pressureTaskApi;
    @Resource
    private ActivityDAO activityDAO;

    private static final Long KB = 1024L;
    private static final Long MB = KB * 1024;
    private static final Long GB = MB * 1024;
    private static final Long TB = GB * 1024;

    private static final String SCRIPT_NAME_SUFFIX = "jmx";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SceneActionOutput start(SceneTaskStartInput input) {
        input.setAssetType(AssetTypeEnum.PRESS_REPORT.getCode());
        input.setResourceId(null);
        return startTask(input);
    }

    private SceneActionOutput startTask(SceneTaskStartInput input) {
        log.info("??????????????????????????????{}", JsonUtil.toJson(input));
        SceneManageQueryOptions options = new SceneManageQueryOptions();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        options.setIncludeSLA(true);
        SceneManageWrapperOutput sceneData = cloudSceneManageService.getSceneManage(input.getSceneId(), options);

        sceneData.setPressureType(PressureSceneEnum.DEFAULT.getCode());
        if (CollectionUtils.isNotEmpty(input.getEnginePlugins())) {
            sceneData.setEnginePlugins(input.getEnginePlugins()
                .stream()
                .filter(Objects::nonNull)
                .map(plugin -> SceneManageWrapperOutput.EnginePluginRefOutput.create(plugin.getPluginId(),
                    plugin.getPluginVersion()))
                .collect(Collectors.toList()));
        } else {
            sceneData.setEnginePlugins(null);
        }

        //??????????????????
        if (Objects.nonNull(input.getSceneInspectInput())) {
            SceneInspectInput inspectInput = input.getSceneInspectInput();
            sceneData.setLoopsNum(inspectInput.getLoopsNum());
            sceneData.setFixedTimer(inspectInput.getFixedTimer());
            sceneData.setInspect(true);
            sceneData.setPressureType(PressureSceneEnum.INSPECTION_MODE.getCode());
        }
        // ????????????????????????
        if (Objects.nonNull(input.getSceneTryRunInput())) {
            SceneTryRunInput sceneTryRunInput = input.getSceneTryRunInput();
            sceneData.setLoopsNum(sceneTryRunInput.getLoopsNum());
            // ???????????????
            sceneData.setConcurrenceNum(sceneTryRunInput.getConcurrencyNum());
            sceneData.setTryRun(true);
            sceneData.setPressureType(PressureSceneEnum.TRY_RUN.getCode());
        }
        //?????????????????????????????????ID?????????????????????????????????????????????
        if (Objects.isNull(input.getSceneInspectInput()) && Objects.isNull(input.getSceneTryRunInput())) {
            SceneManageEntity sceneManageEntity = sceneManageDAO.queueSceneById(input.getSceneId());
            if (Objects.nonNull(sceneManageEntity)) {
                JSONObject features = JsonUtil.parse(sceneManageEntity.getFeatures());
                if (null != features) {
                    Long scriptId = features.getLong("scriptId");
                    stringRedisTemplate.opsForHash().put(
                        String.format(SceneStartCheckConstants.SCENE_KEY, input.getSceneId()),
                        SceneStartCheckConstants.SCRIPT_ID_KEY, String.valueOf(scriptId));
                }
            }
        }
        //????????????????????????
        sceneData.setContinueRead(input.getContinueRead());
        checkStartCondition(sceneData);

        Long sceneId = sceneData.getId();
        Object reportId = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId), PressureStartCache.REPORT_ID);
        ReportResult report = cloudReportService.getReportBaseInfo(Long.valueOf(String.valueOf(reportId)));
        lockFlowIfNecessary(sceneData, input, report);
        sceneData.setResourceId(report.getResourceId());
        SceneActionOutput sceneAction = new SceneActionOutput();
        sceneAction.setData(report.getId());
        notifyStart(sceneData, report);
        // ??????????????????????????????
        if (report.getStatus() == ReportConstants.FINISH_STATUS) {
            //????????????
            JSONObject jb = JSON.parseObject(report.getFeatures());
            sceneAction.setMsg(Arrays.asList(jb.getString(ReportConstants.PRESSURE_MSG).split(",")));
            applyFinish(sceneId);
            return sceneAction;
        }
        //???????????????????????????????????????????????????
        taskStatusCache.cacheStatus(input.getSceneId(), report.getId(), SceneRunTaskStatusEnum.STARTING);
        //??????pod???????????????jmeter????????????????????????????????????????????????
        taskStatusCache.cachePodNum(input.getSceneId(), sceneData.getIpNum());
        //????????????
        sceneTaskEventService.callStartEvent(sceneData, report.getId());

        return sceneAction;
    }

    @Override
    public void stop(Long sceneId) {
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "?????????????????????" + sceneId);
        }
        //???????????????????????????????????????
        if (SceneManageStatusEnum.ifFree(sceneManage.getStatus())) {
            return;
        }
        ReportResult reportResult = reportDao.getReportBySceneId(sceneId);

        if (reportResult != null) {
            sceneTaskEventService.callStopEvent(reportResult);
        }
    }

    /**
     * ??????????????????
     * <p>????????????-????????????</p>
     * <ui>
     * <li>?????????????????????0</li>
     * <li>?????????????????????????????????????????????2</li>
     * </ui>
     *
     * @param req ????????????
     */
    @Override
    public int blotStop(SceneManageIdReq req) {
        Long sceneId = req.getId();
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "????????????????????????????????????!");
        }
        // ?????????????????????????????????????????????
        if (SceneManageStatusEnum.ifFree(sceneManage.getStatus())
            || Objects.equals(SceneManageStatusEnum.RESOURCE_LOCKING.getValue(), sceneManage.getStatus())) {
            String now = String.valueOf(System.currentTimeMillis());
            String resourceKey = PressureStartCache.getSceneResourceKey(sceneId);
            String uniqueKey = String.valueOf(redisClientUtil.hmget(resourceKey, PressureStartCache.UNIQUE_KEY));
            redisClientUtil.setString(PressureStartCache.getScenePreStopKey(sceneId, uniqueKey), now, 5, TimeUnit.MINUTES);
            String resourceId = String.valueOf(redisClientUtil.hmget(resourceKey, PressureStartCache.RESOURCE_ID));
            ResourceContext context = getResourceContext(resourceId);
            if (Objects.isNull(context)) {
                // ??????????????????????????????
                return 1;
            }
            context.setMessage("????????????");
            Event event = new Event();
            event.setEventName(PressureStartCache.PRE_STOP_EVENT);
            event.setExt(context);
            eventCenterTemplate.doEvents(event);
        }
        return 2;
    }

    @Override
    public SceneActionOutput checkSceneTaskStatus(Long sceneId, Long reportId) {
        //?????????????????????id????????????id??????
        SceneActionOutput scene = new SceneActionOutput();
        ReportResult reportResult = null;
        if (reportId != null) {
            reportResult = reportDao.selectById(reportId);
            //?????????????????????????????????????????????????????????
            if (reportResult.getStatus() != null && reportResult.getStatus() > 0) {
                scene.setData(0L);
            } else {
                scene.setData(SceneManageStatusEnum.PRESSURE_TESTING.getValue().longValue());
            }
        } else {
            SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
            if (sceneManage != null) {
                // ??????????????????
                scene.setData(SceneManageStatusEnum.getAdaptStatus(sceneManage.getStatus()).longValue());
                if (sceneManage.getStatus() >= 0) {
                    reportResult = reportDao.getReportBySceneId(sceneId);
                }
            }
        }
        if (reportResult != null) {

            // ??????????????????
            List<String> errorMessageList = Lists.newArrayList();
            // ??????????????????????????????
            SceneRunTaskStatusOutput status = taskStatusCache.getStatus(sceneId, reportResult.getId());
            if (Objects.nonNull(status) && Objects.nonNull(status.getTaskStatus())
                && status.getTaskStatus() == SceneRunTaskStatusEnum.FAILED.getCode()) {
                errorMessageList.add(SceneStopReasonEnum.ENGINE.getType() + ":" + status.getErrorMsg());
            }
            scene.setReportId(reportResult.getId());
            if (StringUtils.isNotEmpty(reportResult.getFeatures())) {
                JSONObject jb = JSON.parseObject(reportResult.getFeatures());
                errorMessageList.add(jb.getString(ReportConstants.FEATURES_ERROR_MSG));
            }
            if (CollectionUtils.isNotEmpty(errorMessageList)) {
                scene.setMsg(errorMessageList);
                //  ??????????????????0,??????????????????
                scene.setData(0L);
            }
        }
        return scene;
    }

    /**
     * ???????????????????????????metric?????????????????????influxdb ?????????
     * ???????????? finished ??????
     */
    @Override
    public void handleSceneTaskEvent(TaskResult taskResult) {
        if (taskResult != null && taskResult.getStatus() != null) {
            switch (taskResult.getStatus()) {
                case FAILED:
                    //????????????
                    testFailed(taskResult);
                    break;
                case STARTED:
                    testStarted(taskResult);
                    break;
                default:
                    log.warn("??????????????????????????????");
                    break;
            }
        }
    }

    @Override
    public String taskResultNotify(SceneTaskNotifyParam param) {
        return sceneTaskEventService.callStartResultEvent(param);
    }

    @Override
    public void updateSceneTaskTps(SceneTaskUpdateTpsInput input) {
        // ??????????????????
        CloudPluginUtils.fillUserData(input);
        dynamicTpsService.set(input);
    }

    @Override
    public double queryAdjustTaskTps(SceneTaskQueryTpsInput input) {
        // ??????????????????
        CloudPluginUtils.fillUserData(input);
        // ?????????????????????
        double result;
        // ???????????????
        Double dynamicValue = dynamicTpsService.get(input);
        // ?????????????????????,??????????????????
        if (dynamicValue != null) {result = dynamicValue;}
        // ???????????????
        else {
            try {
                result = dynamicTpsService.getStatic(input.getReportId(), input.getXpathMd5());
            } catch (Exception e) {
                log.warn("????????????TPS?????????.", e);
                result = 0.0;
            }
        }
        return result;
    }

    @Override
    public Long startFlowDebugTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //????????????????????????id????????????????????????
        String pressureTestSceneName = SceneManageConstant.getFlowDebugSceneName(input.getScriptId());

        //????????????????????????????????????????????????
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);

        //??????????????????????????????
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.FLOW_DEBUG.getCode());
            // ??????????????????????????????????????????
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(5L, "m"));
            input.setPressureMode(PressureModeEnum.FIXED.getCode());
            input.setType(PressureSceneEnum.FLOW_DEBUG.getCode());

            SceneSlaRefInput sceneSlaRefInput = new SceneSlaRefInput();
            sceneSlaRefInput.setRuleName("FLOW_DEBUG_SLA");
            sceneSlaRefInput.setBusinessActivity(new String[] {"-1"});
            RuleBean ruleBean = new RuleBean();
            ruleBean.setIndexInfo(0);
            ruleBean.setCondition(0);
            ruleBean.setDuring(new BigDecimal("10000"));
            ruleBean.setTimes(100);
            sceneSlaRefInput.setRule(ruleBean);
            sceneSlaRefInput.setStatus(0);
            input.setStopCondition(Collections.singletonList(sceneSlaRefInput));
            input.setWarningCondition(new ArrayList<>(0));
            sceneManageId = cloudSceneManageService.addSceneManage(input);

        } else {
            sceneManageId = sceneManageResult.getId();
        }
        //?????????????????????
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        SceneBusinessActivityRefInput activityRefInput = input.getBusinessActivityConfig().get(0);
        sceneTaskStartInput.setAssetType(AssetTypeEnum.ACTIVITY_CHECK.getCode());
        sceneTaskStartInput.setResourceId(activityRefInput.getBusinessActivityId());
        sceneTaskStartInput.setResourceName(activityRefInput.getBusinessActivityName());
        // ??????????????????
        sceneTaskStartInput.setOperateId(input.getOperateId());
        sceneTaskStartInput.setOperateName(input.getOperateName());
        CloudPluginUtils.fillUserData(sceneTaskStartInput);
        preCheckStart(sceneManageId, sceneTaskStartInput);

        String flowDebugKey = PressureStartCache.getFlowDebugKey(sceneManageId);
        redisClientUtil.setString(flowDebugKey, JsonHelper.bean2Json(sceneTaskStartInput));

        StartConditionCheckerContext context = StartConditionCheckerContext.of(sceneManageId);
        context.setUniqueKey(PressureStartCache.getSceneResourceLockingKey(sceneManageId));
        CheckResult checkResult = engineResourceChecker.check(context);
        if (checkResult.getStatus().equals(CheckStatus.FAIL.ordinal())) {
            redisClientUtil.del(flowDebugKey, PressureStartCache.getSceneResourceLockingKey(sceneManageId));
            throw new RuntimeException(checkResult.getMessage());
        }
        //???????????????????????????????????????????????????
        taskStatusCache.cacheStatus(sceneManageId, context.getReportId(), SceneRunTaskStatusEnum.STARTING);
        //????????????id
        return context.getReportId();
    }

    @Override
    public SceneInspectTaskStartOutput startInspectTask(SceneManageWrapperInput input,
        List<EnginePluginInput> enginePlugins) {
        CloudPluginUtils.fillUserData(input);
        SceneInspectTaskStartOutput startOutput = new SceneInspectTaskStartOutput();
        Long sceneManageId = null;
        //????????????????????????id????????????????????????
        String pressureTestSceneName = SceneManageConstant.getInspectSceneName(input.getScriptId());

        //????????????????????????????????????????????????
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);

        //??????????????????????????????
        if (sceneManageResult == null) {
            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.INSPECTION_MODE.getCode());
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(365L, "d"));
            input.setPressureMode(0);
            input.setType(1);
            SceneSlaRefInput sceneSlaRefInput = new SceneSlaRefInput();
            sceneSlaRefInput.setRuleName("INSPECT_SLA");
            sceneSlaRefInput.setBusinessActivity(new String[] {"-2"});
            RuleBean ruleBean = new RuleBean();
            ruleBean.setIndexInfo(0);
            ruleBean.setCondition(0);
            ruleBean.setDuring(new BigDecimal("10000"));
            ruleBean.setTimes(100);
            sceneSlaRefInput.setRule(ruleBean);
            sceneSlaRefInput.setStatus(0);
            input.setStopCondition(Collections.singletonList(sceneSlaRefInput));
            input.setWarningCondition(new ArrayList<>(0));
            sceneManageId = cloudSceneManageService.addSceneManage(input);
        } else {
            SceneManageStatusEnum statusEnum = SceneManageStatusEnum.getSceneManageStatusEnum(
                sceneManageResult.getStatus());
            if (!SceneManageStatusEnum.getFree().contains(statusEnum)) {
                String desc = null != statusEnum ? statusEnum.getDesc() : "";
                String errMsg = "?????????????????????????????????????????????????????????:" + desc;
                log.error("???????????????{}???,??????????????????????????????????????? --> ??????????????????????????????: {}",
                    TakinCloudExceptionEnum.INSPECT_TASK_START_ERROR, desc);
                startOutput.setSceneId(sceneManageId);
                startOutput.setMsg(Collections.singletonList(errMsg));
                return startOutput;
            }
            sceneManageId = sceneManageResult.getId();
        }

        //?????????????????????
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        SceneInspectInput inspectInput = new SceneInspectInput().setFixedTimer(input.getFixTimer()).setLoopsNum(input.getLoopsNum());
        sceneTaskStartInput.setSceneInspectInput(inspectInput);
        sceneTaskStartInput.setContinueRead(false);
        sceneTaskStartInput.setOperateId(input.getOperateId());
        sceneTaskStartInput.setOperateName(input.getOperateName());
        sceneTaskStartInput.setAssetType(AssetTypeEnum.PATRO_SCENE.getCode());
        CloudPluginUtils.fillUserData(sceneTaskStartInput);
        preCheckStart(sceneManageId, sceneTaskStartInput);

        String inspectKey = PressureStartCache.getInspectKey(sceneManageId);
        redisClientUtil.setString(inspectKey, JsonHelper.bean2Json(sceneTaskStartInput));

        StartConditionCheckerContext context = StartConditionCheckerContext.of(sceneManageId);
        context.setUniqueKey(PressureStartCache.getSceneResourceLockingKey(sceneManageId));
        context.setInspect(true);
        CheckResult checkResult = engineResourceChecker.check(context);
        if (checkResult.getStatus().equals(CheckStatus.FAIL.ordinal())) {
            redisClientUtil.del(inspectKey, PressureStartCache.getSceneResourceLockingKey(sceneManageId));
            throw new RuntimeException(checkResult.getMessage());
        }
        startOutput.setSceneId(sceneManageId);
        startOutput.setReportId(context.getReportId());
        //???????????????????????????????????????????????????????????????????????????
        //???????????????????????????????????????????????????
        taskStatusCache.cacheStatus(sceneManageId, context.getReportId(), SceneRunTaskStatusEnum.STARTING);
        return startOutput;
    }

    @Override
    public SceneTaskStopOutput forceStopTask(Long reportId, boolean isNeedFinishReport) {
        SceneTaskStopOutput r = new SceneTaskStopOutput();
        r.setReportId(reportId);
        try {
            ReportResult report = reportDao.selectById(reportId);
            if (null == report) {
                r.addMsg("???????????????");
                return r;
            }

            PressureTaskStopReq request = new PressureTaskStopReq();
            request.setJobId(report.getJobId());
            pressureTaskApi.stop(request);

            // ??????????????????
            if (isNeedFinishReport && ReportConstants.INIT_STATUS == (report.getStatus())
                && null != report.getStartTime()) {
                ReportUpdateParam param = new ReportUpdateParam();
                param.setId(reportId);
                param.setStatus(ReportConstants.RUN_STATUS);
                if (null == report.getEndTime()) {
                    param.setEndTime(Calendar.getInstance().getTime());
                }
                reportDao.updateReport(param);
                generateReport(report.getTaskId());
            } else if (ReportConstants.FINISH_STATUS != (report.getStatus())) {
                cloudReportService.forceFinishReport(reportId);
            }
        } catch (Throwable t) {
            r.addMsg("??????????????????");
            log.error("forceStopTask failed???", t);
        }
        return r;
    }

    @Override
    public SceneInspectTaskStopOutput stopInspectTask(Long sceneId) {
        SceneInspectTaskStopOutput output = new SceneInspectTaskStopOutput();
        output.setSceneId(sceneId);
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (!Objects.isNull(sceneManage)) {
            SceneManageStatusEnum statusEnum = SceneManageStatusEnum.getSceneManageStatusEnum(sceneManage.getStatus());
            if (!SceneManageStatusEnum.getWorking().contains(statusEnum)
                && !SceneManageStatusEnum.getFree().contains(statusEnum)) {
                String errMsg = "?????????????????????????????????????????????????????????:" + (null != statusEnum ? statusEnum.getDesc() : "");
                log.error(errMsg);
                output.setSceneId(sceneManage.getId());
                output.setMsg(Collections.singletonList(errMsg));
                return output;
            } else {
                log.info("??????{} ????????????????????????????????????", sceneId);
                stop(sceneId);
            }
        } else {
            String errMsg = "???????????????:[" + sceneId + "]";
            output.setMsg(Collections.singletonList(errMsg));
        }
        return output;
    }

    @Override
    public SceneTryRunTaskStartOutput startTryRun(SceneManageWrapperInput input,
        List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //????????????????????????id????????????????????????
        String pressureTestSceneName = SceneManageConstant.getTryRunSceneName(input.getScriptDeployId());
        //????????????????????????????????????????????????
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);
        SceneTryRunTaskStartOutput sceneTryRunTaskStartOutput = new SceneTryRunTaskStartOutput();
        CloudPluginUtils.fillUserData(sceneTryRunTaskStartOutput);
        //??????????????????????????????
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.TRY_RUN.getCode());
            input.setConcurrenceNum(1);
            input.setIpNum(1);
            input.setPressureTestTime(new TimeBean(5L, "m"));
            input.setPressureMode(0);

            input.setType(1);
            SceneSlaRefInput sceneSlaRefInput = new SceneSlaRefInput();
            sceneSlaRefInput.setRuleName("TRY_RUN_SLA");
            sceneSlaRefInput.setBusinessActivity(new String[] {"-1"});
            RuleBean ruleBean = new RuleBean();
            ruleBean.setIndexInfo(0);
            ruleBean.setCondition(0);
            ruleBean.setDuring(new BigDecimal("10000"));
            ruleBean.setTimes(100);
            sceneSlaRefInput.setRule(ruleBean);
            sceneSlaRefInput.setStatus(0);
            input.setStopCondition(Collections.singletonList(sceneSlaRefInput));
            input.setWarningCondition(new ArrayList<>(0));
            sceneManageId = cloudSceneManageService.addSceneManage(input);
        } else {
            sceneManageId = sceneManageResult.getId();
        }

        // ??????????????????
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        sceneTaskStartInput.setSceneTryRunInput(new SceneTryRunInput(input.getLoopsNum(), input.getConcurrencyNum()));
        sceneTaskStartInput.setAssetType(AssetTypeEnum.SCRIPT_DEBUG.getCode());
        sceneTaskStartInput.setResourceId(input.getScriptDeployId());
        sceneTaskStartInput.setResourceName(input.getScriptName());
        sceneTaskStartInput.setOperateId(input.getOperateId());
        sceneTaskStartInput.setOperateName(input.getOperateName());
        CloudPluginUtils.fillUserData(sceneTaskStartInput);
        preCheckStart(sceneManageId, sceneTaskStartInput);
        //?????????????????????
        String tryRunKey = PressureStartCache.getTryRunKey(sceneManageId);
        redisClientUtil.setString(tryRunKey, JsonHelper.bean2Json(sceneTaskStartInput));

        StartConditionCheckerContext context = StartConditionCheckerContext.of(sceneManageId);
        context.setUniqueKey(PressureStartCache.getSceneResourceLockingKey(sceneManageId));
        CheckResult checkResult = engineResourceChecker.check(context);
        if (checkResult.getStatus().equals(CheckStatus.FAIL.ordinal())) {
            redisClientUtil.del(tryRunKey, PressureStartCache.getSceneResourceLockingKey(sceneManageId));
            throw new RuntimeException(checkResult.getMessage());
        }
        //???????????????????????????????????????????????????
        taskStatusCache.cacheStatus(sceneManageId, context.getReportId(), SceneRunTaskStatusEnum.STARTING);
        sceneTryRunTaskStartOutput.setSceneId(sceneManageId);
        sceneTryRunTaskStartOutput.setReportId(context.getReportId());
        return sceneTryRunTaskStartOutput;
    }

    @Override
    public SceneTryRunTaskStatusOutput checkTaskStatus(Long sceneId, Long reportId) {
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        Object status = stringRedisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY);
        SceneTryRunTaskStatusOutput output = new SceneTryRunTaskStatusOutput();
        if (Objects.nonNull(status)) {
            SceneRunTaskStatusEnum statusEnum = SceneRunTaskStatusEnum.getTryRunTaskStatusEnumByText(status.toString());
            if (Objects.isNull(statusEnum)) {
                output.setTaskStatus(SceneRunTaskStatusEnum.STARTING.getCode());
                return output;
            }
            output.setTaskStatus(statusEnum.getCode());
            if (statusEnum.equals(SceneRunTaskStatusEnum.FAILED)) {
                Object errorObj = stringRedisTemplate.opsForHash().get(key,
                    SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
                if (Objects.nonNull(errorObj)) {
                    output.setErrorMsg(errorObj.toString());
                }
            }
        } else {
            output.setTaskStatus(SceneRunTaskStatusEnum.STARTING.getCode());
        }
        return output;
    }

    @Override
    public SceneJobStateOutput checkSceneJobStatus(Long sceneId) {
        SceneJobStateOutput state = new SceneJobStateOutput();
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (Objects.isNull(sceneManage)) {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("?????????????????????????????????");
            return state;
        }
        //????????????ID
        String reportId = "";
        if (sceneManage.getStatus() >= 1) {
            Object report = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
                PressureStartCache.REPORT_ID);
            if (Objects.nonNull(report)) {
                reportId = String.valueOf(report);
            }
        } else {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("?????????????????????");
            return state;
        }
        if (StringUtils.isEmpty(reportId)) {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("??????????????????????????????");
            return state;
        }
        Object resource = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
            PressureStartCache.RESOURCE_ID);
        if (redisClientUtil.hasKey(PressureStartCache.getResourceKey(String.valueOf(resource)))) {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_RUNNING);
            state.setMsg("???????????????");
        } else {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("???????????????");
        }
        return state;
    }

    /**
     * ????????????????????????
     */
    private void preCheckStart(Long sceneId, SceneTaskStartInput input) {
        SceneManageQueryOptions options = new SceneManageQueryOptions();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        options.setIncludeSLA(true);
        SceneManageWrapperOutput sceneData = cloudSceneManageService.getSceneManage(sceneId, options);

        // ????????????
        if (null == sceneData.getTenantId()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "??????????????????????????????");
        }
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            AccountInfoExt account = assetExtApi.queryAccount(sceneData.getTenantId(), input.getOperateId());
            if (null == account || account.getBalance().compareTo(sceneData.getEstimateFlow()) < 0) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "?????????????????????");
            }
        }

        if (!SceneManageStatusEnum.ifFree(sceneData.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "????????????????????????????????????");
        }
        //?????????????????????????????????
        SceneScriptRefOutput scriptRefOutput = sceneData.getUploadFile().stream().filter(Objects::nonNull)
            .filter(fileRef -> fileRef.getFileType() == 0 && fileRef.getFileName().endsWith(SCRIPT_NAME_SUFFIX))
            .findFirst()
            .orElse(null);

        boolean jmxCheckResult = checkOutJmx(scriptRefOutput);
        if (!jmxCheckResult) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_JMX_FILE_CHECK_ERROR,
                "??????????????????--??????ID:" + sceneData.getId() + ",???????????????????????????");
        }

        // ?????????????????????job???????????? ??????????????????????????????job??????
        String sceneRunningKey = PressureStartCache.getSceneResourceLockingKey(sceneId);
        if (!redisClientUtil.lockNoExpire(sceneRunningKey, sceneRunningKey)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "?????????" + sceneData.getId() + "???"
                + "??????????????????job,?????????????????????????????????????????????????????????~");
        }
        // ??????????????????????????????
        {
            String disabledKey = "DISABLED";
            String featureString = sceneData.getFeatures();
            Map<String, Object> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, Object>>() {});
            if (feature.containsKey(disabledKey)) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR,
                    "?????????" + sceneData.getId() + "??????????????????????????????????????????????????????????????????????????????????????????");
            }
        }
    }

    /**
     * ???????????????????????? ??????????????? ???????????? ????????????
     * 20200923 ?????? ???????????? ?????????redis ???
     *
     **/
    @Transactional(rollbackFor = Exception.class)
    public synchronized void testStarted(TaskResult taskResult) {
        log.info("??????[{}-{}-{}]????????????????????????", taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId());
        //?????????????????????
        // job????????? ?????? pod????????? ?????? ???????????? ?????? ?????? ???pod ??????
        // ????????????
        String pressureNodeName = ScheduleConstants.getPressureNodeName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getTenantId());

        // cloud?????? redis???????????????increment ????????????????????????????????????key???value
        Long num = stringRedisTemplate.opsForValue().increment(pressureNodeName, 1);
        log.info("????????????pod????????????=???{}???", num);
        if (Long.valueOf(1).equals(num)) {
            // ?????????????????????
            cloudSceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId())
                    .checkEnum(SceneManageStatusEnum.JOB_CREATING)
                    .updateEnum(SceneManageStatusEnum.PRESSURE_NODE_RUNNING).build());
        }

    }

    /**
     * ????????????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    public void testFailed(TaskResult taskResult) {
        log.info("??????[{}]???????????????????????????????????????:{}", taskResult.getSceneId(), taskResult.getMsg());
        Long taskId = taskResult.getTaskId();
        ReportEntity report = reportMapper.selectById(taskId);
        if (report != null && report.getStatus() == ReportConstants.INIT_STATUS) {
            //????????????
            report.setGmtUpdate(new Date());
            report.setIsDeleted(1);
            report.setId(taskId);
            JSONObject json = JsonUtil.parse(report.getFeatures());
            if (null == json) {
                json = new JSONObject();
            }
            json.put(ReportConstants.FEATURES_ERROR_MSG, taskResult.getMsg());
            report.setFeatures(json.toJSONString());
            reportMapper.updateById(report);

            //????????????
            Event event = new Event();
            event.setEventName(PressureStartCache.UNLOCK_FLOW);
            event.setExt(taskResult);
            eventCenterTemplate.doEvents(event);

            ReportResult recentlyReport = reportDao.getRecentlyReport(taskResult.getSceneId());
            if (!taskId.equals(recentlyReport.getId())) {
                log.error("??????????????????????????????????????????????????????????????????????????????,??????id:{},???????????????id:{},?????????????????????id:{}",
                    taskResult.getSceneId(), taskId, recentlyReport.getId());
                return;
            }
            sceneManageDao.getBaseMapper().updateById(new SceneManageEntity() {{
                setId(taskResult.getSceneId());
                setUpdateTime(new Date());
                setStatus(SceneManageStatusEnum.WAIT.getValue());
            }});
        }
    }

    @Override
    public SceneTaskStartCheckOutput sceneStartCsvPositionCheck(SceneTaskStartCheckInput input) {
        //1.????????????????????????
        //2.??????pod??????????????????,????????????
        //3.????????????????????????????????????end
        //4.????????????????????????
        SceneTaskStartCheckOutput output = new SceneTaskStartCheckOutput();
        try {
            SceneManageWrapperOutput sceneManage = cloudSceneManageService.getSceneManage(input.getSceneId(),
                new SceneManageQueryOptions() {{
                    setIncludeBusinessActivity(false);
                    setIncludeScript(true);
                    setIncludeSLA(false);
                }});
            input.setPodNum(sceneManage.getIpNum());
            long sceneId = input.getSceneId();
            List<SceneScriptRefOutput> uploadFile = sceneManage.getUploadFile();
            if (CollectionUtils.isEmpty(uploadFile)) {
                output.setHasUnread(false);
                return output;
            }
            Collection<FileInfo> fileInfoList = uploadFile.stream().filter(file -> file.getFileType() == 1)
                .map(file -> {
                    if (file.getFileName().endsWith(".csv")) {
                        FileInfo info = new FileInfo();
                        info.setFileName(file.getFileName());
                        info.setSplit(file.getIsSplit() != null && file.getIsSplit() == 1);
                        return info;
                    }
                    return null;
                }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(fileInfoList)) {
                output.setHasUnread(false);
                cleanCachedPosition(sceneId);
                return output;
            }
            //????????????????????????
            JSONObject features = JSONObject.parseObject(sceneManage.getFeatures());
            Boolean scriptChange = compareScript(sceneId, features.getString("scriptId"));
            if (!scriptChange) {
                output.setHasUnread(false);
                cleanCachedPosition(sceneId);
                return output;
            }
            if (input.getPodNum() > 0) {
                if (!comparePod(sceneId, input.getPodNum())) {
                    output.setHasUnread(false);
                    cleanCachedPosition(sceneId);
                    return output;
                }
                String key = String.format(SceneStartCheckConstants.SCENE_KEY, sceneId);
                Map<Object, Object> positionMap = redisTemplate.opsForHash().entries(key);
                if (MapUtils.isNotEmpty(positionMap)) {
                    for (FileInfo info : fileInfoList) {
                        comparePosition(output, sceneId, info.getFileName(), input.getPodNum(), info.isSplit(),
                            positionMap);
                        if (!output.getHasUnread()) {
                            cleanCachedPosition(sceneId);
                            output.setFileReadInfos(new ArrayList<>());
                            return output;
                        }
                    }
                } else {
                    cleanCachedPosition(sceneId);
                    output.setHasUnread(false);
                    output.setFileReadInfos(new ArrayList<>());
                    return output;
                }
            }
            output.setHasUnread(true);
        } catch (Exception e) {
            log.error("?????????????????????????????????????????????ID???{}???????????????:{}", input.getSceneId(), e.getMessage());
            output.setHasUnread(false);
        }
        return output;
    }

    @Override
    public void writeBalance(AssetBalanceExt balanceExt) {
        log.warn("???????????????????????????:{}", JSON.toJSONString(balanceExt));
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            assetExtApi.writeBalance(balanceExt);
        }
    }

    private Boolean compareScript(long sceneId, String scriptId) {
        Object scriptIdObj = stringRedisTemplate.opsForHash().get(
            String.format(SceneStartCheckConstants.SCENE_KEY, sceneId), SceneStartCheckConstants.SCRIPT_ID_KEY);
        return scriptIdObj != null && scriptId.equals(scriptIdObj.toString());
    }

    private boolean comparePod(long sceneId, int podNum) {
        Object podObj = stringRedisTemplate.opsForHash().get(ScheduleConstants.SCHEDULE_POD_NUM,
            String.valueOf(sceneId));
        if (Objects.nonNull(podObj)) {
            int cachedPodNum = Integer.parseInt(podObj.toString());
            return cachedPodNum == podNum;
        }
        return false;
    }

    private void comparePosition(SceneTaskStartCheckOutput output, long sceneId, String fileName, int podNum,
        boolean isSplit,
        Map<Object, Object> positionMap) {
        SceneBigFileSliceEntity sliceEntity = fileSliceService.getOneByParam(new FileSliceRequest() {{
            setSceneId(sceneId);
            setFileName(fileName);
        }});
        if (Objects.isNull(sliceEntity) || sliceEntity.getSliceCount() != podNum || StringUtils.isBlank(
            sliceEntity.getSliceInfo())) {
            output.setHasUnread(false);
            return;
        }

        List<FileReadInfo> fileReadInfos = output.getFileReadInfos();
        if (CollectionUtils.isEmpty(fileReadInfos)) {
            fileReadInfos = new ArrayList<>();
        }
        List<StartEndPair> startEndPairs = JSONArray.parseArray(sliceEntity.getSliceInfo(), StartEndPair.class);
        long fileSize = startEndPairs.stream().filter(Objects::nonNull)
            .mapToLong(StartEndPair::getEnd)
            .filter(Objects::nonNull)
            .max()
            .orElse(0L);
        if (fileSize == 0) {
            output.setHasUnread(false);
            return;
        }
        //??????????????????????????????????????????
        if (isSplit) {
            long readSize = 0;
            for (int i = 0; i < podNum; i++) {
                StartEndPair pair = startEndPairs.get(i);
                Object o = positionMap.get(String.format(SceneStartCheckConstants.FILE_POD_FIELD_KEY, fileName, i + 1));
                if (Objects.isNull(o)) {
                    output.setHasUnread(false);
                    return;
                }
                SceneFileReadPosition position = JSONUtil.toBean(o.toString(), SceneFileReadPosition.class);
                if (position.getReadPosition() < pair.getStart() || position.getReadPosition() > pair.getEnd()) {
                    output.setHasUnread(false);
                    return;
                }
                readSize += position.getReadPosition() - pair.getStart();
            }
            String fileSizeStr = getPositionSize(fileSize);
            String readSizeStr = getPositionSize(readSize);
            FileReadInfo info = new FileReadInfo() {{
                setFileName(fileName);
                setFileSize(fileSizeStr);
                setReadSize(readSizeStr);
            }};
            fileReadInfos.add(info);
            output.setHasUnread(true);
            output.setFileReadInfos(fileReadInfos);
        }
        //???????????????????????????pod????????????????????????
        else {
            FileReadInfo info = new FileReadInfo();
            info.setFileName(fileName);
            long readSize = 0;
            for (Entry<Object, Object> entry : positionMap.entrySet()) {
                if (!SceneStartCheckConstants.SCRIPT_ID_KEY.equals(entry.getKey().toString())
                    && entry.getKey().toString().contains(fileName)) {
                    SceneFileReadPosition readPosition = JSONUtil.toBean(entry.getValue().toString(),
                        SceneFileReadPosition.class);
                    if (readPosition.getReadPosition() > readSize) {
                        readSize = readPosition.getReadPosition();
                    }
                }
            }
            String fileSizeStr = getPositionSize(fileSize);
            String readSizeStr = getPositionSize(readSize);
            info.setReadSize(readSizeStr);
            info.setFileSize(fileSizeStr);
            fileReadInfos.add(info);
            output.setHasUnread(true);
            output.setFileReadInfos(fileReadInfos);
        }
    }

    @Override
    public void cleanCachedPosition(Long sceneId) {
        String key = String.format(SceneStartCheckConstants.SCENE_KEY, sceneId);
        stringRedisTemplate.delete(key);
    }

    private String getPositionSize(Long position) {
        if (position > TB) {
            return position / TB + "TB";
        }
        if (position > GB) {
            return position / GB + "GB";
        }
        if (position > MB) {
            return position / MB + "MB";
        }
        if (position > KB) {
            return position / KB + "KB";
        }
        return position + "B";
    }

    @Override
    public PressureTaskEntity initPressureTask(SceneManageWrapperOutput scene, SceneTaskStartInput input) {
        PressureTaskEntity entity = new PressureTaskEntity();
        entity.setSceneId(scene.getId());
        entity.setResourceId(scene.getResourceId());
        entity.setSceneName(scene.getPressureTestSceneName());
        entity.setStatus(PressureTaskStateEnum.INITIALIZED.ordinal());
        entity.setGmtCreate(new Date());
        entity.setOperateId(input.getOperateId());
        entity.setUserId(WebPluginUtils.traceUserId());
        entity.setTenantId(scene.getTenantId());
        entity.setEnvCode(scene.getEnvCode());
        pressureTaskDAO.save(entity);
        pressureTaskVarietyDAO.save(PressureTaskVarietyEntity.of(entity.getId(), PressureTaskStateEnum.INITIALIZED));
        return entity;
    }

    /**
     * ???????????????
     */
    @Override
    public ReportEntity initReport(SceneManageWrapperOutput scene, SceneTaskStartInput input,
        PressureTaskEntity pressureTask) {
        ReportEntity report = new ReportEntity();
        report.setSceneId(scene.getId());
        report.setTaskId(pressureTask.getId());
        report.setResourceId(pressureTask.getResourceId());
        report.setConcurrent(scene.getConcurrenceNum());
        report.setStatus(ReportConstants.INIT_STATUS);
        // ?????????
        report.setEnvCode(scene.getEnvCode());
        report.setTenantId(scene.getTenantId());
        report.setOperateId(input.getOperateId());
        //????????????????????????
        report.setUserId(CloudPluginUtils.getUserId());
        report.setSceneName(scene.getPressureTestSceneName());

        if (StringUtils.isNotBlank(scene.getFeatures())) {
            JSONObject features = JsonUtil.parse(scene.getFeatures());
            if (null != features && features.containsKey(SceneManageConstant.FEATURES_SCRIPT_ID)) {
                report.setScriptId(features.getLong(SceneManageConstant.FEATURES_SCRIPT_ID));
            }
        }
        List<SceneBusinessActivityRefOutput> businessActivityConfig = scene.getBusinessActivityConfig();
        Integer sumTps = CommonUtil.sum(businessActivityConfig,
            SceneBusinessActivityRefOutput::getTargetTPS);

        report.setTps(sumTps);
        report.setPressureType(scene.getPressureType());
        report.setType(scene.getType());
        if (StringUtils.isNotBlank(scene.getScriptAnalysisResult())) {
            report.setScriptNodeTree(JsonPathUtil.deleteNodes(scene.getScriptAnalysisResult()).jsonString());
        }
        report.setCalibrationStatus(0);
        report.setPtConfig(scene.getPtConfig());
        reportMapper.insert(report);
        associateTaskAndReport(pressureTask, report);
        Long reportId = report.getId();

        ActivityQueryParam param = new ActivityQueryParam();
        param.setActivityIds(businessActivityConfig.stream()
            .map(SceneBusinessActivityRefOutput::getBusinessActivityId).collect(Collectors.toList()));
        Map<Long, ActivityListResult> activityMap = activityDAO.getActivityList(param)
            .stream().collect(Collectors.toMap(ActivityListResult::getActivityId, Function.identity()));
        businessActivityConfig.forEach(activity -> {
            ReportBusinessActivityDetail reportBusinessActivityDetail = new ReportBusinessActivityDetail();
            reportBusinessActivityDetail.setReportId(reportId);
            reportBusinessActivityDetail.setSceneId(scene.getId());
            reportBusinessActivityDetail.setBusinessActivityId(activity.getBusinessActivityId());
            reportBusinessActivityDetail.setBusinessActivityName(activity.getBusinessActivityName());
            reportBusinessActivityDetail.setApplicationIds(activity.getApplicationIds());
            reportBusinessActivityDetail.setBindRef(activity.getBindRef());
            if (null != activity.getTargetTPS()) {
                reportBusinessActivityDetail.setTargetTps(new BigDecimal(activity.getTargetTPS()));
            }
            if (null != activity.getTargetRT()) {
                reportBusinessActivityDetail.setTargetRt(new BigDecimal(activity.getTargetRT()));
            }
            reportBusinessActivityDetail.setTargetSuccessRate(activity.getTargetSuccessRate());
            reportBusinessActivityDetail.setTargetSa(activity.getTargetSA());

            ActivityListResult activityListResult = activityMap.get(activity.getBusinessActivityId());
            if (Objects.nonNull(activityListResult)) {
                JSONObject futures = new JSONObject();
                futures.put(ReportConstants.ACTIVITY_ENTRANCE, activityListResult.getEntrace());
                futures.put(ReportConstants.ACTIVITY_TYPE, activityListResult.getBusinessType());
                reportBusinessActivityDetail.setFeatures(futures.toJSONString());
            }
            reportBusinessActivityDetailDao.insert(reportBusinessActivityDetail);
        });
        saveNonTargetNode(scene.getId(), reportId, report.getScriptNodeTree(), businessActivityConfig);
        return report;
    }


    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param sceneId                ??????ID
     * @param reportId               ??????ID
     * @param scriptNodeTree         ?????????
     * @param businessActivityConfig ????????????????????????
     */
    private void saveNonTargetNode(Long sceneId, Long reportId, String scriptNodeTree,
        List<SceneBusinessActivityRefOutput> businessActivityConfig) {
        if (StringUtils.isBlank(scriptNodeTree) || org.apache.commons.collections.CollectionUtils.isEmpty(
            businessActivityConfig)) {
            return;
        }
        List<String> bindRefList = businessActivityConfig.stream().filter(Objects::nonNull)
            .map(SceneBusinessActivityRefOutput::getBindRef)
            .collect(Collectors.toList());
        List<ReportBusinessActivityDetail> resultList = new ArrayList<>();
        List<ScriptNode> testPlanNodeList = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.TEST_PLAN.name());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(testPlanNodeList)
            && testPlanNodeList.size() == 1) {
            ScriptNode scriptNode = testPlanNodeList.get(0);
            fillNonTargetActivityDetail(sceneId, reportId, scriptNode, resultList);
        }
        List<ScriptNode> threadGroupNodes = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.THREAD_GROUP.name());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(threadGroupNodes)) {
            threadGroupNodes.stream().filter(Objects::nonNull)
                .forEach(node -> fillNonTargetActivityDetail(sceneId, reportId, node, resultList));
        }
        List<ScriptNode> controllerNodes = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
            NodeTypeEnum.CONTROLLER.name());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(controllerNodes)) {
            controllerNodes.stream().filter(Objects::nonNull)
                .filter(node -> !bindRefList.contains(node.getXpathMd5()))
                .forEach(node -> fillNonTargetActivityDetail(sceneId, reportId, node, resultList));
        }
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(resultList)) {
            resultList.stream().filter(Objects::nonNull)
                .forEach(detail -> reportBusinessActivityDetailDao.insert(detail));
        }

    }

    /**
     * ???????????????????????????
     *
     * @param sceneId    ??????ID
     * @param scriptNode ????????????
     * @param detailList ??????
     */
    private void fillNonTargetActivityDetail(Long sceneId, Long reportId, ScriptNode scriptNode,
        List<ReportBusinessActivityDetail> detailList) {
        ReportBusinessActivityDetail detail = new ReportBusinessActivityDetail();
        detail.setTargetTps(new BigDecimal(-1));
        detail.setTargetRt(new BigDecimal(-1));
        detail.setTargetSa(new BigDecimal(-1));
        detail.setTargetSuccessRate(new BigDecimal(-1));
        detail.setSceneId(sceneId);
        detail.setReportId(reportId);
        detail.setBusinessActivityId(-1L);
        detail.setBusinessActivityName(scriptNode.getTestName());
        detail.setBindRef(scriptNode.getXpathMd5());
        detailList.add(detail);
    }

    private void associateTaskAndReport(PressureTaskEntity pressureTask, ReportEntity report) {
        PressureTaskEntity tmp = new PressureTaskEntity();
        tmp.setId(pressureTask.getId());
        tmp.setReportId(report.getId());
        pressureTaskDAO.updateById(tmp);
    }

    private void notifyStart(SceneManageWrapperOutput scene, ReportResult report) {
        cloudSceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(scene.getId(), report.getId(), scene.getCustomId())
                .checkEnum(SceneManageStatusEnum.RESOURCE_LOCKING)
                .updateEnum(SceneManageStatusEnum.STARTING).build());
    }

    @IntrestFor(event = PressureStartCache.CHECK_SUCCESS_EVENT, order = 1)
    public void tryRun(Event event) {
        ResourceContext context = (ResourceContext)event.getExt();
        Long sceneId = context.getSceneId();
        String tryRunKey = PressureStartCache.getTryRunKey(sceneId);
        if (redisClientUtil.hasKey(tryRunKey)) {
            String tryRun = redisClientUtil.getString(tryRunKey);
            redisClientUtil.del(tryRunKey);
            try {
                SceneTaskStartInput input = JsonHelper.json2Bean(tryRun, SceneTaskStartInput.class);
                fillContext(input);
                startTask(input);
            } catch (Exception e) {
                startFail(context.getResourceId(), e.getMessage());
            }
        }
    }

    @IntrestFor(event = PressureStartCache.CHECK_SUCCESS_EVENT, order = 2)
    public void flowDebug(Event event) {
        ResourceContext context = (ResourceContext)event.getExt();
        Long sceneId = context.getSceneId();
        String flowDebugKey = PressureStartCache.getFlowDebugKey(sceneId);
        if (redisClientUtil.hasKey(flowDebugKey)) {
            String flowDebug = redisClientUtil.getString(flowDebugKey);
            redisClientUtil.del(flowDebugKey);
            try {
                SceneTaskStartInput input = JsonHelper.json2Bean(flowDebug, SceneTaskStartInput.class);
                fillContext(input);
                startTask(input);
            } catch (Exception e) {
                startFail(context.getResourceId(), e.getMessage());
            }
        }
    }

    @IntrestFor(event = PressureStartCache.CHECK_SUCCESS_EVENT, order = 3)
    public void inspect(Event event) {
        ResourceContext context = (ResourceContext)event.getExt();
        Long sceneId = context.getSceneId();
        String inspectKey = PressureStartCache.getInspectKey(sceneId);
        if (redisClientUtil.hasKey(inspectKey)) {
            String inspect = redisClientUtil.getString(inspectKey);
            redisClientUtil.del(inspectKey);
            try {
                SceneTaskStartInput input = JsonHelper.json2Bean(inspect, SceneTaskStartInput.class);
                fillContext(input);
                startTask(input);
            } catch (Exception e) {
                startFail(context.getResourceId(), e.getMessage());
            }
        }
    }

    private boolean checkOutJmx(SceneScriptRefOutput uploadFile) {
        if (Objects.nonNull(uploadFile) && StringUtils.isNotBlank(uploadFile.getUploadPath())) {
            String fileMd5 = MD5Utils.getInstance().getMD5(new File(uploadFile.getUploadPath()));
            if (StringUtils.isNotBlank(uploadFile.getFileMd5())) {
                return uploadFile.getFileMd5().equals(fileMd5);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param input     {@link SceneTaskStartInput}
     * @param report    {@link Report}
     * @param sceneData {@link SceneManageWrapperOutput}
     */
    private void frozenAccountFlow(SceneTaskStartInput input, ReportResult report, SceneManageWrapperOutput sceneData) {
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            //??????????????????ID
            Long resourceId = input.getResourceId();
            if (AssetTypeEnum.PRESS_REPORT.getCode().equals(input.getAssetType())) {
                resourceId = report.getId();
            }
            AssetInvoiceExt<List<AssetBillExt>> invoice = new AssetInvoiceExt<>();
            invoice.setSceneId(sceneData.getId());
            invoice.setTaskId(report.getId());
            invoice.setResourceId(resourceId);
            invoice.setResourceType(input.getAssetType());
            invoice.setResourceName(input.getResourceName());
            invoice.setOperateId(input.getOperateId());
            invoice.setOperateName(input.getOperateName());
            invoice.setCustomerId(report.getTenantId());
            AssetBillExt.TimeBean pressureTestTime = new AssetBillExt.TimeBean(sceneData.getTotalTestTime(),
                TimeUnitEnum.SECOND.getValue());
            String testTimeCost = DataUtils.formatTime(sceneData.getTotalTestTime());
            if (MapUtils.isNotEmpty(sceneData.getThreadGroupConfigMap())) {
                List<AssetBillExt> bills = sceneData.getThreadGroupConfigMap().values().stream()
                    .filter(Objects::nonNull)
                    .map(config -> {
                        AssetBillExt bill = new AssetBillExt();
                        bill.setIpNum(sceneData.getIpNum());
                        bill.setConcurrenceNum(config.getThreadNum());
                        bill.setPressureTestTime(pressureTestTime);
                        bill.setPressureMode(config.getMode());
                        bill.setPressureScene(sceneData.getPressureType());
                        bill.setPressureType(config.getType());
                        if (null != config.getRampUp()) {
                            AssetBillExt.TimeBean rampUp = new AssetBillExt.TimeBean(config.getRampUp().longValue(),
                                config.getRampUpUnit());
                            bill.setIncreasingTime(rampUp);
                        }
                        bill.setStep(config.getSteps());
                        bill.setPressureTestTimeCost(testTimeCost);
                        return bill;
                    })
                    .collect(Collectors.toList());
                invoice.setData(bills);
            } else if (null != sceneData.getConcurrenceNum()) {
                AssetBillExt bill = new AssetBillExt();
                bill.setIpNum(sceneData.getIpNum());
                bill.setConcurrenceNum(sceneData.getConcurrenceNum());
                bill.setPressureTestTime(pressureTestTime);
                bill.setPressureMode(PressureModeEnum.FIXED.getCode());
                bill.setPressureScene(sceneData.getPressureType());
                bill.setPressureType(ThreadGroupTypeEnum.CONCURRENCY.getCode());
                bill.setPressureTestTimeCost(DataUtils.formatTime(sceneData.getTotalTestTime()));
                invoice.setData(Lists.newArrayList(bill));
            }
            try {
                Response<String> res = assetExtApi.lock(invoice);
                if (null != res && res.isSuccess() && StringUtils.isNotBlank(res.getData())) {
                    ReportUpdateParam rp = new ReportUpdateParam();
                    rp.setId(report.getId());
                    JSONObject features = JsonUtil.parse(report.getFeatures());
                    if (null == features) {
                        features = new JSONObject();
                    }
                    features.put("lockId", res.getData());
                    reportDao.updateReport(rp);
                } else {
                    throw new RuntimeException("??????????????????");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    private void startFail(String resourceId, String message) {
        StartFailEventSource source = new StartFailEventSource();
        source.setMessage(message);
        source.setContext(getResourceContext(resourceId));
        Event event = new Event();
        event.setEventName(PressureStartCache.START_FAILED);
        event.setExt(source);
        eventCenterTemplate.doEvents(event);
    }

    private void applyFinish(Long sceneId) {
        Object resource = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
            PressureStartCache.RESOURCE_ID);
        String resourceId = String.valueOf(resource);
        ResourceContext context = getResourceContext(resourceId);
        Event event = new Event();
        event.setEventName(PressureStartCache.PRESSURE_END);
        event.setExt(context);
        eventCenterTemplate.doEvents(event);
        releaseResource(resourceId);
    }

    private void checkStartCondition(SceneManageWrapperOutput sceneData) {
        if (!SceneManageStatusEnum.RESOURCE_LOCKING.getValue().equals(sceneData.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "??????????????????????????????????????????");
        }
    }

    private void lockFlowIfNecessary(SceneManageWrapperOutput sceneData, SceneTaskStartInput input,
        ReportResult report) {
        if (!Objects.equals(input.getAssetType(), AssetTypeEnum.PRESS_REPORT.getCode())
            && redisClientUtil.lockNoExpire(PressureStartCache.getLockFlowKey(report.getId()),
            String.valueOf(System.currentTimeMillis()))) {
            frozenAccountFlow(input, report, sceneData);
        }
    }

    private void fillContext(SceneTaskStartInput input) {
        TenantCommonExt commonExt = new TenantCommonExt();
        commonExt.setTenantId(input.getTenantId());
        commonExt.setEnvCode(input.getEnvCode());
        commonExt.setTenantAppKey(input.getUserAppKey());
        commonExt.setSource(ContextSourceEnum.JOB_SCRIPT_DEBUG.getCode());
        WebPluginUtils.setTraceTenantContext(commonExt);
    }
}
