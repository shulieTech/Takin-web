package io.shulie.takin.cloud.biz.notify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.cloud.biz.cache.SceneTaskStatusCache;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskVarietyDAO;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskVarietyEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * ?????????????????????????????????
 */
@Slf4j
@Component
public class PressureEventCenter extends AbstractIndicators {

    @Resource
    private PressureTaskDAO pressureTaskDAO;
    @Resource
    private PressureTaskVarietyDAO pressureTaskVarietyDAO;
    @Resource
    private ReportDao reportDao;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private SceneTaskStatusCache taskStatusCache;
    @Resource
    private CloudReportService cloudReportService;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private SceneTaskService sceneTaskService;
    @Resource
    private CloudAsyncService cloudAsyncService;

    /**
     * ??????????????????
     * 1????????????????????????????????????
     * 2?????????????????????????????????
     *
     * @param event ?????????
     */
    @IntrestFor(event = PressureStartCache.CHECK_SUCCESS_EVENT, order = 0)
    public void callCheckSuccess(Event event) {
        ResourceContext ext = (ResourceContext)event.getExt();
        String resourceId = ext.getResourceId();
        String resourceKey = PressureStartCache.getResourceKey(resourceId);
        redisClientUtil.hmset(resourceKey, PressureStartCache.CHECK_STATUS, CheckStatus.SUCCESS.ordinal());
        pressureTaskDAO.updateStatus(ext.getTaskId(), PressureTaskStateEnum.STARTING, null);
        cloudAsyncService.checkStartTimeout(resourceId);
    }

    /**
     * ??????????????????
     * 1?????????????????????
     * 2???????????????
     * 3???????????????
     * 4???????????????
     * 5?????????????????????
     *
     * @param event ?????????
     */
    @IntrestFor(event = PressureStartCache.CHECK_FAIL_EVENT)
    public void callCheckFailed(Event event) {
        dealCheckFail((ResourceContext)event.getExt());
    }

    /**
     * ??????????????????
     * 1??????????????????????????????????????????????????????
     * 2???????????????
     * 3???????????????/????????????
     * 4????????????????????? [ ????????????activity?????? ]
     * 5????????????????????? [ ????????????activity?????? ]
     * 6?????????????????? failed ??????
     * 7?????????????????????
     * 8???????????????
     *
     * @param event ?????????
     */
    @IntrestFor(event = PressureStartCache.START_FAILED)
    public void callStartFail(Event event) {
        StartFailEventSource source = (StartFailEventSource)event.getExt();
        ResourceContext context = source.getContext();
        if (context != null) {
            dealStartFail(context, source.getMessage());
        }
    }

    /**
     * ??????????????????
     * 1??????????????????????????? [ ??????????????? ]
     * 2??????????????????????????????????????????????????????
     *
     * @param event ?????????
     */
    @IntrestFor(event = PressureStartCache.RUNNING_FAILED)
    public void callRunningFail(Event event) {
        StopEventSource source = (StopEventSource)event.getExt();
        ResourceContext context = source.getContext();
        String message = source.getMessage();
        Long taskId = context.getTaskId();
        String resourceId = context.getResourceId();
        boolean exception = false;
        try {
            stopJob(resourceId, context.getJobId());
        } catch (Throwable e) {
            pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.UNUSUAL, message + " | " + e.getMessage());
            exception = true;
        }
        boolean noInterrupt = !source.isInterrupt();
        if (noInterrupt) {
            setTryRunTaskFailInfo(context.getSceneId(), context.getReportId(), context.getTenantId(), message);
        }
        if (!exception && redisClientUtil.lockStopFlagExpire(PressureStartCache.getStopFlag(resourceId), message)) {
            if (noInterrupt) {
                pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.UNUSUAL, message);
            }
            pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.STOPPING, null);
            endDefaultPressureIfNecessary(context);
        }
    }

    @IntrestFor(event = PressureStartCache.PRE_STOP_EVENT)
    public void callPreStop(Event event) {
        ResourceContext context = (ResourceContext)event.getExt();
        String resourceKey = PressureStartCache.getSceneResourceKey(context.getSceneId());
        if (!redisClientUtil.hExists(resourceKey, PressureStartCache.START_FLAG)) {
            dealCheckFail(context);
            return;
        }
        String resourceId = context.getResourceId();
        String message = context.getMessage();
        Object job = redisClientUtil.hmget(PressureStartCache.getResourceKey(resourceId), PressureStartCache.JOB_ID);
        if (redisClientUtil.hasKey(PressureStartCache.getJmeterStartFirstKey(resourceId))) {
            context.setJobId(Long.valueOf(String.valueOf(job)));
            Event failEvent = new Event();
            StopEventSource source = new StopEventSource();
            source.setContext(context);
            source.setMessage(message);
            failEvent.setExt(source);
            callRunningFail(failEvent);
            return;
        }
        dealStartFail(context, message);
    }

    @IntrestFor(event = PressureStartCache.PRESSURE_END)
    public void pressureEnd(Event event) {
        ResourceContext context = (ResourceContext)event.getExt();
        Long sceneId = context.getSceneId();
        Long reportId = context.getReportId();
        clearAllCache(sceneId, context.getResourceId());
        clearOthersPressureModelCache(sceneId);
        taskStatusCache.cacheStatus(sceneId, reportId, SceneRunTaskStatusEnum.ENDED);
        redisClientUtil.del(RedisClientUtil.getLockKey(PressureStartCache.getLockFlowKey(reportId)),
            RedisClientUtil.getLockKey(PressureStartCache.getReleaseFlowKey(reportId)));
        removeReportKey(reportId);
    }

    @IntrestFor(event = PressureStartCache.UNLOCK_FLOW)
    public void unLockFlow(Event event) {
        TaskResult ext = (TaskResult)event.getExt();
        unLockFlow(ext.getTaskId(), ext.getTenantId());
    }

    private void dealStartFail(ResourceContext context, String message) {
        String stopFlag = PressureStartCache.getStopFlag(context.getResourceId());
        if (redisClientUtil.lockStopFlagExpire(stopFlag, message)) {
            Long taskId = context.getTaskId();
            Long reportId = context.getReportId();
            setTryRunTaskFailInfo(context.getSceneId(), reportId, context.getTenantId(), message);
            pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.UNUSUAL, message);
            pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.STOPPING, null);
            unLockFlow(reportId, context.getTenantId());
            try {
                stopJob(context.getResourceId(), context.getJobId());
            } catch (Exception ignore) {}
            updateSceneFailed(context.getSceneId(), SceneManageStatusEnum.STOP);
            if (!redisClientUtil.hasKey(PressureStartCache.getReportCachedKey(reportId))) {
                sceneTaskService.cacheReportKey(reportId);
            }
            notifyFinish(context);
            endDefaultPressureIfNecessary(context);
        }
    }

    private void dealCheckFail(ResourceContext context) {
        String resourceId = context.getResourceId();
        String message = context.getMessage();
        if (StringUtils.isBlank(resourceId) ||
            (redisClientUtil.lockStopFlagExpire(PressureStartCache.getStopFlag(context.getResourceId()), message)
                && redisClientUtil.lockExpire(PressureStartCache.getResourceCheckFailKey(resourceId),
                String.valueOf(System.currentTimeMillis()), 1, TimeUnit.MINUTES))) {
            Long reportId = context.getReportId();
            unLockFlow(reportId, context.getTenantId());
            releaseResource(resourceId);
            deleteReport(reportId, message);
            updateSceneFailed(context.getSceneId(), SceneManageStatusEnum.FAILED);
            checkFailed(context, message);
            pressureTaskDAO.updateStatus(context.getTaskId(), PressureTaskStateEnum.INACTIVE, null);
        }
    }

    /**
     * ?????????????????????????????????
     *  @param reportId ??????Id
     * @param message  ????????????
     */
    private void deleteReport(Long reportId, String message) {
        ReportResult report = reportDao.selectById(reportId);
        if (Objects.nonNull(report)) {
            report.setIsDeleted(1);
            report.setGmtUpdate(new Date());
            fillFeatures(report, message);
            ReportUpdateParam param = BeanUtil.copyProperties(report, ReportUpdateParam.class);
            reportDao.updateReport(param);
        }
    }

    private void checkFailed(ResourceContext context, String message) {
        String resourceId = context.getResourceId();
        Long taskId = context.getTaskId();
        Long sceneId = context.getSceneId();
        if (StringUtils.isNotBlank(resourceId)) {
            redisClientUtil.del(PressureStartCache.getResourcePodSuccessKey(resourceId),
                PressureStartCache.getPodStartFirstKey(resourceId), PressureStartCache.getPodHeartbeatKey(resourceId));
            String resourceKey = PressureStartCache.getResourceKey(resourceId);
            Map<String, Object> param = new HashMap<>(4);
            param.put(PressureStartCache.ERROR_MESSAGE, message);
            param.put(PressureStartCache.CHECK_STATUS, CheckStatus.FAIL.ordinal());
            redisClientUtil.hmset(resourceKey, param);
            redisClientUtil.expire(resourceKey, 60);
        }
        Long reportId = context.getReportId();
        setTryRunTaskFailInfo(sceneId, reportId, context.getTenantId(), message);
        removeReportKey(reportId);
        PressureTaskEntity entity = new PressureTaskEntity();
        entity.setId(taskId);
        entity.setStatus(PressureTaskStateEnum.RESOURCE_LOCK_FAILED.ordinal());
        entity.setExceptionMsg(message);
        entity.setIsDeleted(1);
        entity.setGmtUpdate(new Date());
        pressureTaskDAO.updateById(entity);
        pressureTaskVarietyDAO.save(
            PressureTaskVarietyEntity.of(taskId, PressureTaskStateEnum.RESOURCE_LOCK_FAILED, message));
        if (redisClientUtil.unlock(PressureStartCache.getSceneResourceLockingKey(sceneId), context.getUniqueKey())) {
            redisClientUtil.delete(PressureStartCache.getSceneResourceKey(sceneId));
        }
    }

    /**
     * ??????????????????
     *
     * @param sceneId    ??????Id
     * @param resourceId ??????Id
     */
    private void clearAllCache(Long sceneId, String resourceId) {
        redisClientUtil.del(PressureStartCache.clearCacheKey(resourceId, sceneId).toArray(new String[0]));
    }

    /**
     * ????????????????????????????????????
     *
     * @param sceneId ??????Id
     */
    private void clearOthersPressureModelCache(Long sceneId) {
        String tryRunKey = PressureStartCache.getTryRunKey(sceneId);
        if (redisClientUtil.hasKey(tryRunKey)) {
            redisClientUtil.del(tryRunKey);
        }
        String flowDebugKey = PressureStartCache.getFlowDebugKey(sceneId);
        if (redisClientUtil.hasKey(flowDebugKey)) {
            redisClientUtil.del(flowDebugKey);
        }
        String inspectKey = PressureStartCache.getInspectKey(sceneId);
        if (redisClientUtil.hasKey(inspectKey)) {
            redisClientUtil.del(inspectKey);
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param sceneId  ??????Id
     * @param reportId ??????Id
     * @param tenantId ??????Id
     * @param errorMsg ????????????
     */
    protected void setTryRunTaskFailInfo(Long sceneId, Long reportId, Long tenantId, String errorMsg) {
        log.info("??????????????????--sceneId:???{}???,reportId:???{}???,tenantId:???{}???,errorMsg:???{}???",
            sceneId, reportId, tenantId, errorMsg);
        String tryRunTaskKey = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, reportId);
        stringRedisTemplate.opsForHash().put(tryRunTaskKey,
            SceneTaskRedisConstants.SCENE_RUN_TASK_STATUS_KEY, SceneRunTaskStatusEnum.FAILED.getText());
        if (StringUtils.isNotBlank(errorMsg)) {
            stringRedisTemplate.opsForHash().put(tryRunTaskKey, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR, errorMsg);
        }
    }

    /**
     * ????????????
     *
     * @param reportId ??????Id
     * @param tenantId ??????Id
     */
    public void unLockFlow(Long reportId, Long tenantId) {
        if (Objects.isNull(reportId) || !shouldUnlockFlow(reportId)) {
            return;
        }
        ReportResult report = reportDao.selectById(reportId);
        if (report != null) {
            String amountLockId;
            JSONObject json = JsonUtil.parse(report.getFeatures());
            if (null == json) {
                json = new JSONObject();
            }
            amountLockId = json.getString("lockId");
            //????????????
            AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
            if (assetExtApi != null) {
                boolean unLock;
                try {
                    if (StringUtils.isNotBlank(amountLockId)) {
                        unLock = assetExtApi.unlock(tenantId, amountLockId);
                    } else {
                        unLock = assetExtApi.unlock(report.getTenantId(), String.valueOf(reportId));
                    }
                    if (!unLock) {
                        log.error("?????????????????????");
                    }
                } catch (Exception e) {
                    log.error("??????????????????", e);
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param reportId ??????Id
     * @return true-??????
     */
    private boolean shouldUnlockFlow(Long reportId) {
        String lockFlowKey = RedisClientUtil.getLockKey(PressureStartCache.getLockFlowKey(reportId));
        boolean locked = redisClientUtil.hasKey(lockFlowKey);
        if (locked && redisClientUtil.lockExpire(PressureStartCache.getReleaseFlowKey(reportId),
            String.valueOf(System.currentTimeMillis()), 5, TimeUnit.MINUTES)) {
            redisClientUtil.del(lockFlowKey);
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @param sceneId ??????Id
     */
    private void updateSceneFailed(Long sceneId, SceneManageStatusEnum statusEnum) {
        sceneManageDAO.getBaseMapper().update(null,
            Wrappers.lambdaUpdate(SceneManageEntity.class)
                .set(SceneManageEntity::getStatus, statusEnum.getValue())
                .eq(SceneManageEntity::getId, sceneId));
    }

    private void fillFeatures(ReportResult report, String message) {
        JSONObject json = JsonUtil.parse(report.getFeatures());
        if (null == json) {
            json = new JSONObject();
        }
        json.put(ReportConstants.FEATURES_ERROR_MSG, message);
        report.setFeatures(json.toJSONString());
    }

    public static class AmdbCalibrationException extends RuntimeException {

        public AmdbCalibrationException(String message) {
            super(message);
        }
    }

    public static class CloudCalibrationException extends RuntimeException {
        public CloudCalibrationException(String message) {
            super(message);
        }
    }

    private void endDefaultPressureIfNecessary(ResourceContext context) {
        // ???????????????????????????
        if (Objects.equals(context.getPressureType(), PressureSceneEnum.INSPECTION_MODE.getCode())) {
            Long reportId = context.getReportId();
            Long sceneId = context.getSceneId();
            Long tenantId = context.getTenantId();
            cloudReportService.updateReportFeatures(reportId, ReportConstants.FINISH_STATUS, null, null);
            cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
                .checkEnum(SceneManageStatusEnum.getAll()).updateEnum(SceneManageStatusEnum.WAIT).build());
            pressureTaskDAO.updateStatus(context.getTaskId(), PressureTaskStateEnum.INACTIVE, null);
            Event event = new Event();
            event.setExt(context);
            pressureEnd(event);
        }
    }

    private void removeReportKey(Long reportId) {
        final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
        redisTemplate.opsForList().remove(WebRedisKeyConstant.getTaskList(), 0, reportKey);
        redisTemplate.opsForValue().getOperations().delete(reportKey);
    }
}
