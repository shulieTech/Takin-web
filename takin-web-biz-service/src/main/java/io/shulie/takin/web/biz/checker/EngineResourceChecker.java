package io.shulie.takin.web.biz.checker;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.resource.CloudResourceApi;
import io.shulie.takin.adapter.api.model.request.resource.ResourceCheckRequest;
import io.shulie.takin.adapter.api.model.request.resource.ResourceLockRequest;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneTaskStartInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EngineResourceChecker extends AbstractIndicators implements StartConditionChecker {

    @Resource
    private StrategyConfigService strategyConfigService;
    @Resource
    private CloudResourceApi cloudResourceApi;
    @Resource
    private CloudSceneManageService cloudSceneManageService;
    @Resource
    private CloudSceneTaskService cloudSceneTaskService;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private AppConfig appConfig;
    @Resource
    private CloudAsyncService cloudAsyncService;
    @Resource
    private PressureTaskDAO pressureTaskDAO;
    @Resource
    private ReportDao reportDao;

    @Override
    public CheckResult check(StartConditionCheckerContext context) throws TakinCloudException {
        String resourceId = context.getResourceId();
        return StringUtils.isBlank(resourceId) ? firstCheck(context) : getResourceStatus(resourceId);
    }

    private CheckResult firstCheck(StartConditionCheckerContext context) {
        try {
            fillContextIfNecessary(context);
            initTaskAndReportIfNecessary(context);
            SceneManageWrapperOutput sceneData = context.getSceneData();
            sceneData.setStrategy(getStrategy(context));
            Boolean checkResult = cloudResourceApi.check(buildCheckRequest(sceneData));
            if (!Boolean.TRUE.equals(checkResult)) {
                return CheckResult.fail(type(), "压力机资源不足");
            }
            pressureTaskDAO.updateStatus(context.getTaskId(), PressureTaskStateEnum.CHECKED, null);
            // 锁定资源：异步接口，每个pod启动成功都会回调一次回调接口
            String resourceId = lockResource(context);
            context.setResourceId(resourceId);
            afterLocking(context);
            return getResourceStatus(resourceId);
        } catch (Exception e) {
            return CheckResult.fail(type(), e.getMessage());
        }
    }

    private CheckResult getResourceStatus(String resourceId) {
        String statusKey = PressureStartCache.getResourceKey(resourceId);
        Object redisStatus = redisClientUtil.hmget(statusKey, PressureStartCache.CHECK_STATUS);
        if (redisStatus == null) {
            return new CheckResult(type(), CheckStatus.FAIL.ordinal(), resourceId, "未找到启动中的任务");
        }
        int status = Integer.parseInt(String.valueOf(redisStatus));
        String message = null;
        Object errorMessage;
        if (status == CheckStatus.FAIL.ordinal()
            && Objects.nonNull(errorMessage = redisClientUtil.hmget(statusKey, PressureStartCache.ERROR_MESSAGE))) {
            message = String.valueOf(errorMessage);
        }
        return new CheckResult(type(), status, resourceId, message);
    }

    private String lockResource(StartConditionCheckerContext context) {
        return cloudResourceApi.lock(buildLockRequest(context.getSceneData()));
    }

    private void afterLocking(StartConditionCheckerContext context) {
        associateResource(context);
        initCache(context);
        SceneManageWrapperOutput sceneData = context.getSceneData();
        cloudSceneManageService.updateSceneLifeCycle(
            UpdateStatusBean.build(context.getSceneId(), context.getReportId(), sceneData.getTenantId())
                .checkEnum(SceneManageStatusEnum.WAIT, SceneManageStatusEnum.FAILED, SceneManageStatusEnum.STOP,
                    SceneManageStatusEnum.FORCE_STOP)
                .updateEnum(SceneManageStatusEnum.RESOURCE_LOCKING).build());
        pressureTaskDAO.updateStatus(context.getTaskId(), PressureTaskStateEnum.RESOURCE_LOCKING, null);
        cloudAsyncService.checkPodStartedTask(context);
    }

    private void initCache(StartConditionCheckerContext context) {
        SceneManageWrapperOutput sceneData = context.getSceneData();
        String resourceId = context.getResourceId();
        Map<String, Object> param = new HashMap<>(32);
        param.put(PressureStartCache.CHECK_STATUS, CheckStatus.PENDING.ordinal());
        param.put(PressureStartCache.POD_NUM, sceneData.getIpNum());
        param.put(PressureStartCache.TENANT_ID, sceneData.getTenantId());
        param.put(PressureStartCache.ENV_CODE, sceneData.getEnvCode());
        param.put(PressureStartCache.SCENE_ID, String.valueOf(sceneData.getId()));
        param.put(PressureStartCache.TASK_ID, context.getTaskId());
        param.put(PressureStartCache.REPORT_ID, context.getReportId());
        param.put(PressureStartCache.UNIQUE_KEY, context.getUniqueKey());
        param.put(PressureStartCache.USER_ID, WebPluginUtils.traceUserId());
        param.put(PressureStartCache.PT_TEST_TIME, sceneData.getPressureTestSecond());
        redisClientUtil.hmset(PressureStartCache.getResourceKey(resourceId), param);

        // 巡检等需要设置
        Map<String, Object> sceneParam = new HashMap<>(4);
        sceneParam.put(PressureStartCache.REPORT_ID, context.getReportId());
        sceneParam.put(PressureStartCache.TASK_ID, context.getTaskId());
        sceneParam.put(PressureStartCache.RESOURCE_ID, context.getResourceId());
        sceneParam.put(PressureStartCache.UNIQUE_KEY, context.getUniqueKey());
        redisClientUtil.hmset(PressureStartCache.getSceneResourceKey(sceneData.getId()), sceneParam);
    }

    private StrategyConfigExt getStrategy(StartConditionCheckerContext context) {
        StrategyConfigExt config = strategyConfigService.getCurrentStrategyConfig();
        if (config == null) {
            throw new RuntimeException("未配置策略");
        }
        if (context.isInspect()) {
            return getInspectStrategy(config.getPressureEngineImage());
        }
        return config;
    }

    private void fillContextIfNecessary(StartConditionCheckerContext context) {
        if (Objects.isNull(context.getSceneData())) {
            SceneManageQueryOptions options = new SceneManageQueryOptions();
            options.setIncludeBusinessActivity(true);
            options.setIncludeScript(true);
            SceneManageWrapperOutput sceneManage = cloudSceneManageService.getSceneManage(context.getSceneId(), options);
            context.setSceneData(sceneManage);
            context.setSceneId(sceneManage.getId());
            context.setTenantId(sceneManage.getTenantId());
        }
        if (Objects.isNull(context.getInput())) {
            SceneTaskStartInput input = new SceneTaskStartInput();
            input.setOperateId(WebPluginUtils.traceUserId());
            context.setInput(input);
        }
    }

    private void initTaskAndReportIfNecessary(StartConditionCheckerContext context) {
        if (!context.isInitTaskAndReport()) {
            SceneManageWrapperOutput sceneData = context.getSceneData();
            SceneTaskStartInput input = context.getInput();
            PressureTaskEntity pressureTask = cloudSceneTaskService.initPressureTask(sceneData, input);
            ReportEntity report = cloudSceneTaskService.initReport(sceneData, input, pressureTask);
            context.setTaskId(pressureTask.getId());
            context.setReportId(report.getId());
        }
        context.setInitTaskAndReport(true);
    }

    private void associateResource(StartConditionCheckerContext context) {
        PressureTaskEntity entity = new PressureTaskEntity();
        entity.setId(context.getTaskId());
        entity.setResourceId(context.getResourceId());
        entity.setGmtUpdate(new Date());
        pressureTaskDAO.updateById(entity);

        ReportUpdateParam param = new ReportUpdateParam();
        param.setId(context.getReportId());
        param.setResourceId(context.getResourceId());
        reportDao.updateReport(param);
    }

    @Override
    public String type() {
        return "resource";
    }

    @Override
    public int getOrder() {
        return 4;
    }

    private ResourceCheckRequest buildCheckRequest(SceneManageWrapperOutput sceneData) {
        StrategyConfigExt strategy = sceneData.getStrategy();
        ResourceCheckRequest request = new ResourceCheckRequest();
        request.setCpu(strategy.getCpuNum().toPlainString());
        request.setMemory(strategy.getMemorySize().toPlainString());
        request.setNumber(sceneData.getIpNum());
        request.setLimitCpu(strategy.getLimitCpuNum().toPlainString());
        request.setLimitMemory(strategy.getLimitMemorySize().toPlainString());
        return request;
    }

    private ResourceLockRequest buildLockRequest(SceneManageWrapperOutput sceneData) {
        StrategyConfigExt strategy = sceneData.getStrategy();
        ResourceLockRequest request = new ResourceLockRequest();
        request.setCpu(strategy.getCpuNum().toPlainString());
        request.setMemory(strategy.getMemorySize().toPlainString());
        request.setLimitCpu(strategy.getLimitCpuNum().toPlainString());
        request.setLimitMemory(strategy.getLimitMemorySize().toPlainString());
        request.setNumber(sceneData.getIpNum());
        request.setImage(strategy.getPressureEngineImage());
        request.setCallbackUrl(appConfig.getCallbackUrl());
        return request;
    }

    private StrategyConfigExt getInspectStrategy(String image) {
        StrategyConfigExt strategy = new StrategyConfigExt();
        strategy.setCpuNum(new BigDecimal("0.05"));
        strategy.setMemorySize(new BigDecimal("200"));
        strategy.setLimitCpuNum(new BigDecimal("0.2"));
        strategy.setLimitMemorySize(new BigDecimal("300"));
        strategy.setPressureEngineImage(image);
        return strategy;
    }
}
