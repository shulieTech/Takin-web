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
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.dao.report.TReportMapper;
import com.pamirs.takin.cloud.entity.domain.entity.report.Report;
import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneFileReadPosition;
import com.pamirs.takin.cloud.entity.domain.vo.file.FileSliceRequest;
import com.pamirs.takin.cloud.entity.domain.vo.report.SceneTaskNotifyParam;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.entrypoint.resource.CloudResourceApi;
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
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
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
 * @author 莫问
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
    private TReportMapper tReportMapper;
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
    private CloudAsyncService cloudAsyncService;
    @Resource
    private CloudResourceApi cloudResourceApi;
    @Resource
    private PressureTaskApi pressureTaskApi;

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
        log.info("启动任务接收到入参：{}", JsonUtil.toJson(input));
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

        //设置巡检参数
        if (Objects.nonNull(input.getSceneInspectInput())) {
            SceneInspectInput inspectInput = input.getSceneInspectInput();
            sceneData.setLoopsNum(inspectInput.getLoopsNum());
            sceneData.setFixedTimer(inspectInput.getFixedTimer());
            sceneData.setInspect(true);
            sceneData.setPressureType(PressureSceneEnum.INSPECTION_MODE.getCode());
        }
        // 设置脚本试跑参数
        if (Objects.nonNull(input.getSceneTryRunInput())) {
            SceneTryRunInput sceneTryRunInput = input.getSceneTryRunInput();
            sceneData.setLoopsNum(sceneTryRunInput.getLoopsNum());
            // 传入并发数
            sceneData.setConcurrenceNum(sceneTryRunInput.getConcurrencyNum());
            sceneData.setTryRun(true);
            sceneData.setPressureType(PressureSceneEnum.TRY_RUN.getCode());
        }
        //缓存本次压测使用的脚本ID，在记录文件读取位点的时候使用
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
        //文件是否继续读取
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
        // 报告已经完成，则退出
        if (report.getStatus() == ReportConstants.FINISH_STATUS) {
            //失败状态
            JSONObject jb = JSON.parseObject(report.getFeatures());
            sceneAction.setMsg(Arrays.asList(jb.getString(ReportConstants.PRESSURE_MSG).split(",")));
            applyFinish(sceneId);
            return sceneAction;
        }
        //设置缓存，用以检查压测场景启动状态
        taskStatusCache.cacheStatus(input.getSceneId(), report.getId(), SceneRunTaskStatusEnum.STARTING);
        //缓存pod数量，上传jmeter日志时判断是否所有文件都上传完成
        taskStatusCache.cachePodNum(input.getSceneId(), sceneData.getIpNum());
        //广播事件
        sceneTaskEventService.callStartEvent(sceneData, report.getId());

        return sceneAction;
    }

    @Override
    public void stop(Long sceneId) {
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "压测场景不存在" + sceneId);
        }
        //压测场景已经关闭，不做处理
        if (SceneManageStatusEnum.ifFree(sceneManage.getStatus())) {
            return;
        }
        ReportResult reportResult = reportDao.getReportBySceneId(sceneId);

        if (reportResult != null) {
            sceneTaskEventService.callStopEvent(reportResult);
        }
    }

    /**
     * 停止场景测试
     * <p>直接模式-手工补偿</p>
     * <ui>
     * <li>重置场景状态为0</li>
     * <li>重置对应的最新的压测报告状态为2</li>
     * </ui>
     *
     * @param req 场景主键
     */
    @Override
    public int blotStop(SceneManageIdReq req) {
        Long sceneId = req.getId();
        SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
        if (sceneManage == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, "停止压测失败，场景不存在!");
        }
        // 校验接口返回才允许点击取消压测
        if (SceneManageStatusEnum.ifFree(sceneManage.getStatus())
            || Objects.equals(SceneManageStatusEnum.RESOURCE_LOCKING.getValue(), sceneManage.getStatus())) {
            String now = String.valueOf(System.currentTimeMillis());
            String resourceKey = PressureStartCache.getSceneResourceKey(sceneId);
            String uniqueKey = String.valueOf(redisClientUtil.hmget(resourceKey, PressureStartCache.UNIQUE_KEY));
            redisClientUtil.setString(PressureStartCache.getScenePreStopKey(sceneId, uniqueKey), now, 5, TimeUnit.MINUTES);
            String resourceId = String.valueOf(redisClientUtil.hmget(resourceKey, PressureStartCache.RESOURCE_ID));
            ResourceContext context = getResourceContext(resourceId);
            context.setMessage("取消压测");
            Event event = new Event();
            event.setEventName(PressureStartCache.PRE_STOP_EVENT);
            event.setExt(context);
            eventCenterTemplate.doEvents(event);
        }
        return 2;
    }

    @Override
    public SceneActionOutput checkSceneTaskStatus(Long sceneId, Long reportId) {
        //为如果传入报告id，以报告id为准
        SceneActionOutput scene = new SceneActionOutput();
        ReportResult reportResult = null;
        if (reportId != null) {
            reportResult = reportDao.selectById(reportId);
            //如果报告状态是已结束，查询结果为已结束
            if (reportResult.getStatus() != null && reportResult.getStatus() > 0) {
                scene.setData(0L);
            } else {
                scene.setData(SceneManageStatusEnum.PRESSURE_TESTING.getValue().longValue());
            }
        } else {
            SceneManageEntity sceneManage = sceneManageDAO.getSceneById(sceneId);
            if (sceneManage != null) {
                // 监测启动状态
                scene.setData(SceneManageStatusEnum.getAdaptStatus(sceneManage.getStatus()).longValue());
                if (sceneManage.getStatus() >= 0) {
                    reportResult = reportDao.getReportBySceneId(sceneId);
                }
            }
        }
        if (reportResult != null) {

            // 记录错误信息
            List<String> errorMessageList = Lists.newArrayList();
            // 检查压测引擎返回内容
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
                //  前端只有等于0,才会显示错误
                scene.setData(0L);
            }
        }
        return scene;
    }

    /**
     * 报告生成触发条件，metric数据完全上传至influxdb 才触发
     * 可以查看 finished 事件
     */
    @Override
    public void handleSceneTaskEvent(TaskResult taskResult) {
        if (taskResult != null && taskResult.getStatus() != null) {
            switch (taskResult.getStatus()) {
                case FAILED:
                    //启动失败
                    testFailed(taskResult);
                    break;
                case STARTED:
                    testStarted(taskResult);
                    break;
                default:
                    log.warn("其他状态暂时无需处理");
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
        // 补充租户信息
        CloudPluginUtils.fillUserData(input);
        dynamicTpsService.set(input);
    }

    @Override
    public double queryAdjustTaskTps(SceneTaskQueryTpsInput input) {
        // 补充租户信息
        CloudPluginUtils.fillUserData(input);
        // 声明返回值字段
        double result;
        // 获取动态值
        Double dynamicValue = dynamicTpsService.get(input);
        // 如果动态值为空,则获取静态值
        if (dynamicValue != null) {result = dynamicValue;}
        // 获取静态值
        else {
            try {
                result = dynamicTpsService.getStatic(input.getReportId(), input.getXpathMd5());
            } catch (Exception e) {
                log.warn("获取静态TPS值失败.", e);
                result = 0.0;
            }
        }
        return result;
    }

    @Override
    public Long startFlowDebugTask(SceneManageWrapperInput input, List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_FLOW_DEBUG + input.getTenantId() + "_"
            + input.getScriptDeployId();

        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);

        //不存在，新增压测场景
        if (sceneManageResult == null) {

            input.setPressureTestSceneName(pressureTestSceneName);
            input.setPressureType(PressureSceneEnum.FLOW_DEBUG.getCode());
            // 后续会根据传入并发数进行修改
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
        //启动该压测场景
        SceneTaskStartInput sceneTaskStartInput = new SceneTaskStartInput();
        sceneTaskStartInput.setSceneId(sceneManageId);
        sceneTaskStartInput.setEnginePlugins(enginePlugins);
        sceneTaskStartInput.setContinueRead(false);
        SceneBusinessActivityRefInput activityRefInput = input.getBusinessActivityConfig().get(0);
        sceneTaskStartInput.setAssetType(AssetTypeEnum.ACTIVITY_CHECK.getCode());
        sceneTaskStartInput.setResourceId(activityRefInput.getBusinessActivityId());
        sceneTaskStartInput.setResourceName(activityRefInput.getBusinessActivityName());
        // 设置用户主键
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
        //设置缓存，用以检查压测场景启动状态
        taskStatusCache.cacheStatus(sceneManageId, context.getReportId(), SceneRunTaskStatusEnum.STARTING);
        //返回报告id
        return context.getReportId();
    }

    @Override
    public SceneInspectTaskStartOutput startInspectTask(SceneManageWrapperInput input,
        List<EnginePluginInput> enginePlugins) {
        CloudPluginUtils.fillUserData(input);
        SceneInspectTaskStartOutput startOutput = new SceneInspectTaskStartOutput();
        Long sceneManageId = null;
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_INSPECT + input.getTenantId() + "_"
            + input.getScriptId();

        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);

        //不存在，新增压测场景
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
                String errMsg = "启动巡检场景失败，场景前置状态校验失败:" + desc;
                log.error("异常代码【{}】,异常内容：启动巡检场景失败 --> 场景前置状态校验失败: {}",
                    TakinCloudExceptionEnum.INSPECT_TASK_START_ERROR, desc);
                startOutput.setSceneId(sceneManageId);
                startOutput.setMsg(Collections.singletonList(errMsg));
                return startOutput;
            }
            sceneManageId = sceneManageResult.getId();
        }

        //启动该压测场景
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
        //开始试跑就设置一个状态，后面区分试跑任务和正常压测
        //设置缓存，用以检查压测场景启动状态
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
                r.addMsg("任务不存在");
                return r;
            }

            PressureTaskStopReq request = new PressureTaskStopReq();
            request.setJobId(report.getJobId());
            pressureTaskApi.stop(request);

            // 触发强制停止
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
            r.addMsg("程序抛异常了");
            log.error("forceStopTask failed！", t);
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
                String errMsg = "停止巡检场景失败，场景前置状态校验失败:" + (null != statusEnum ? statusEnum.getDesc() : "");
                log.error(errMsg);
                output.setSceneId(sceneManage.getId());
                output.setMsg(Collections.singletonList(errMsg));
                return output;
            } else {
                log.info("任务{} ，原因：巡检场景触发停止", sceneId);
                stop(sceneId);
            }
        } else {
            String errMsg = "场景不存在:[" + sceneId + "]";
            output.setMsg(Collections.singletonList(errMsg));
        }
        return output;
    }

    @Override
    public SceneTryRunTaskStartOutput startTryRun(SceneManageWrapperInput input,
        List<EnginePluginInput> enginePlugins) {
        Long sceneManageId;
        CloudPluginUtils.fillUserData(input);
        //首先根据脚本实例id构建压测场景名称
        String pressureTestSceneName = SceneManageConstant.SCENE_MANAGER_TRY_RUN + input.getTenantId() + "_" + input
            .getScriptDeployId();
        //根据场景名称查询是否已经存在场景
        SceneManageListResult sceneManageResult = sceneManageDAO.queryBySceneName(pressureTestSceneName);
        SceneTryRunTaskStartOutput sceneTryRunTaskStartOutput = new SceneTryRunTaskStartOutput();
        CloudPluginUtils.fillUserData(sceneTryRunTaskStartOutput);
        //不存在，新增压测场景
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

        // 前置环境校验
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
        //启动该压测场景
        String tryRunKey = PressureStartCache.getTryRunKey(sceneManageId);
        redisClientUtil.setString(tryRunKey, JsonHelper.bean2Json(sceneTaskStartInput));

        StartConditionCheckerContext context = StartConditionCheckerContext.of(sceneManageId);
        context.setUniqueKey(PressureStartCache.getSceneResourceLockingKey(sceneManageId));
        CheckResult checkResult = engineResourceChecker.check(context);
        if (checkResult.getStatus().equals(CheckStatus.FAIL.ordinal())) {
            redisClientUtil.del(tryRunKey, PressureStartCache.getSceneResourceLockingKey(sceneManageId));
            throw new RuntimeException(checkResult.getMessage());
        }
        //设置缓存，用以检查压测场景启动状态
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
            state.setMsg("未查询到相应的压测场景");
            return state;
        }
        //获取报告ID
        String reportId = "";
        if (sceneManage.getStatus() >= 1) {
            Object report = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
                PressureStartCache.REPORT_ID);
            if (Objects.nonNull(report)) {
                reportId = String.valueOf(report);
            }
        } else {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("压测任务未启动");
            return state;
        }
        if (StringUtils.isEmpty(reportId)) {
            state.setState(SceneManageConstant.SCENE_TASK_JOB_STATUS_NONE);
            state.setMsg("未获取到相应压测报告");
            return state;
        }
        Object resource = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
            PressureStartCache.RESOURCE_ID);
        if (redisClientUtil.hasKey(PressureStartCache.getResourceKey(String.valueOf(resource)))) {
            state.setMsg("任务执行中");
        } else {
            state.setMsg("任务已停止");
        }
        return state;
    }

    /**
     * 场景启动前置校验
     */
    private void preCheckStart(Long sceneId, SceneTaskStartInput input) {
        SceneManageQueryOptions options = new SceneManageQueryOptions();
        options.setIncludeBusinessActivity(true);
        options.setIncludeScript(true);
        options.setIncludeSLA(true);
        SceneManageWrapperOutput sceneData = cloudSceneManageService.getSceneManage(sceneId, options);

        // 流量判断
        if (null == sceneData.getTenantId()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "场景没有绑定客户信息");
        }
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            AccountInfoExt account = assetExtApi.queryAccount(sceneData.getTenantId(), input.getOperateId());
            if (null == account || account.getBalance().compareTo(sceneData.getEstimateFlow()) < 0) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "压测流量不足！");
            }
        }

        if (!SceneManageStatusEnum.ifFree(sceneData.getStatus())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "当前场景不为待启动状态！");
        }
        //检测脚本文件是否有变更
        SceneScriptRefOutput scriptRefOutput = sceneData.getUploadFile().stream().filter(Objects::nonNull)
            .filter(fileRef -> fileRef.getFileType() == 0 && fileRef.getFileName().endsWith(SCRIPT_NAME_SUFFIX))
            .findFirst()
            .orElse(null);

        boolean jmxCheckResult = checkOutJmx(scriptRefOutput);
        if (!jmxCheckResult) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_JMX_FILE_CHECK_ERROR,
                "启动压测场景--场景ID:" + sceneData.getId() + ",脚本文件校验失败！");
        }

        // 判断场景是否有job正在执行 一个场景只能保证一个job执行
        String sceneRunningKey = PressureStartCache.getSceneResourceLockingKey(sceneId);
        if (!redisClientUtil.lockNoExpire(sceneRunningKey, sceneRunningKey)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "场景【" + sceneData.getId() + "】"
                + "存在未删除的job,请等待删除或者人为判断是否可以手工删除~");
        }
        // 校验是否与场景同步了
        {
            String disabledKey = "DISABLED";
            String featureString = sceneData.getFeatures();
            Map<String, Object> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, Object>>() {});
            if (feature.containsKey(disabledKey)) {
                throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR,
                    "场景【" + sceneData.getId() + "】对应的业务流程发生变更，未能自动匹配，请手动编辑后启动压测");
            }
        }
    }

    /**
     * 压测任务正常开启 这里实际是 压力节点 启动成功
     * 20200923 报告 开始时间 记录在redis 中
     *
     **/
    @Transactional(rollbackFor = Exception.class)
    public synchronized void testStarted(TaskResult taskResult) {
        log.info("场景[{}-{}-{}]启动新的压测任务", taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId());
        //场景压测进行中
        // job创建中 改成 pod工作中 隐式 状态严格 更新 解决 多pod 问题
        // 进行计数
        String pressureNodeName = ScheduleConstants.getPressureNodeName(taskResult.getSceneId(), taskResult.getTaskId(),
            taskResult.getTenantId());

        // cloud集群 redis同步操作，increment 直接拿数据，无需重新获取key的value
        Long num = stringRedisTemplate.opsForValue().increment(pressureNodeName, 1);
        log.info("当前启动pod成功数量=【{}】", num);
        if (Long.valueOf(1).equals(num)) {
            // 启动只更新一次
            cloudSceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(taskResult.getSceneId(), taskResult.getTaskId(), taskResult.getTenantId())
                    .checkEnum(SceneManageStatusEnum.JOB_CREATING)
                    .updateEnum(SceneManageStatusEnum.PRESSURE_NODE_RUNNING).build());
        }

    }

    /**
     * 压测任务启动失败
     */
    @Transactional(rollbackFor = Exception.class)
    public void testFailed(TaskResult taskResult) {
        log.info("场景[{}]压测任务启动失败，失败原因:{}", taskResult.getSceneId(), taskResult.getMsg());
        Long taskId = taskResult.getTaskId();
        ReportEntity report = reportMapper.selectById(taskId);
        if (report != null && report.getStatus() == ReportConstants.INIT_STATUS) {
            //删除报表
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

            //释放流量
            Event event = new Event();
            event.setEventName(PressureStartCache.UNLOCK_FLOW);
            event.setExt(taskResult);
            eventCenterTemplate.doEvents(event);

            ReportResult recentlyReport = reportDao.getRecentlyReport(taskResult.getSceneId());
            if (!taskId.equals(recentlyReport.getId())) {
                log.error("更新压测生命周期，所更新的报告不是压测场景的最新报告,场景id:{},更新的报告id:{},当前最新的报告id:{}",
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
        //1.查询缓存是否有值
        //2.查询pod数量和文件名,进行比较
        //3.查询缓存值是否小于预分片end
        //4.查询脚本是否变更
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
            //判断脚本是否变更
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
            log.error("获取文件读取位点信息失败：场景ID：{}，错误信息:{}", input.getSceneId(), e.getMessage());
            output.setHasUnread(false);
        }
        return output;
    }

    @Override
    public void writeBalance(AssetBalanceExt balanceExt) {
        log.warn("回写流量接收到入参:{}", JSON.toJSONString(balanceExt));
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
        //文件拆分，计算已读、文件大小
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
        //文件不拆分，取所有pod中读的最多的提示
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
     * 初始化报表
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
        // 初始化
        report.setEnvCode(scene.getEnvCode());
        report.setTenantId(scene.getTenantId());
        report.setOperateId(input.getOperateId());
        //负责人默认启动人
        report.setUserId(CloudPluginUtils.getUserId());
        report.setSceneName(scene.getPressureTestSceneName());

        if (StringUtils.isNotBlank(scene.getFeatures())) {
            JSONObject features = JsonUtil.parse(scene.getFeatures());
            if (null != features && features.containsKey(SceneManageConstant.FEATURES_SCRIPT_ID)) {
                report.setScriptId(features.getLong(SceneManageConstant.FEATURES_SCRIPT_ID));
            }
        }
        Integer sumTps = CommonUtil.sum(scene.getBusinessActivityConfig(),
            SceneBusinessActivityRefOutput::getTargetTPS);

        report.setTps(sumTps);
        report.setPressureType(scene.getPressureType());
        report.setType(scene.getType());
        if (StringUtils.isNotBlank(scene.getScriptAnalysisResult())) {
            report.setScriptNodeTree(JsonPathUtil.deleteNodes(scene.getScriptAnalysisResult()).jsonString());
        }
        report.setCalibrationStatus(0);
        reportMapper.insert(report);
        associateTaskAndReport(pressureTask, report);
        Long reportId = report.getId();
        scene.getBusinessActivityConfig().forEach(activity -> {
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
            reportBusinessActivityDetailDao.insert(reportBusinessActivityDetail);
        });
        saveNonTargetNode(scene.getId(), reportId, report.getScriptNodeTree(), scene.getBusinessActivityConfig());
        return report;
    }


    /**
     * 把节点树中的测试计划、线程组、控制器当作业务活动插入到报告关联的业务活动中
     *
     * @param sceneId                场景ID
     * @param reportId               报告ID
     * @param scriptNodeTree         节点树
     * @param businessActivityConfig 场景业务活动信息
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
     * 计算子节点的目标值
     *
     * @param sceneId    场景ID
     * @param scriptNode 目标节点
     * @param detailList 结果
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
                cloudAsyncService.checkStartTimeout(sceneId);
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
                cloudAsyncService.checkStartTimeout(sceneId);
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
                cloudAsyncService.checkStartTimeout(sceneId);
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
     * 冻结流量
     *
     * @param input     {@link SceneTaskStartInput}
     * @param report    {@link Report}
     * @param sceneData {@link SceneManageWrapperOutput}
     */
    private void frozenAccountFlow(SceneTaskStartInput input, ReportResult report, SceneManageWrapperOutput sceneData) {
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (assetExtApi != null) {
            //得到数据来源ID
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
                    throw new RuntimeException("流量冻结失败");
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
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR, "当前场景不为资源锁定中状态！");
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
