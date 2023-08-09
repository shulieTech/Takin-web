package io.shulie.takin.cloud.biz.notify.processor.jmeter;

import java.util.Date;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.cache.SceneTaskStatusCache;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.CloudNotifyProcessor;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
import io.shulie.takin.cloud.constant.enums.CallbackType;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.model.callback.basic.PressureExample;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PressureStartNotifyProcessor extends AbstractIndicators
    implements CloudNotifyProcessor<PressureStartNotifyParam> {

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    protected CloudSceneManageService cloudSceneManageService;

    @Resource
    private ReportDao reportDao;

    @Resource
    private CloudAsyncService cloudAsyncService;

    @Resource
    private SceneTaskStatusCache taskStatusCache;
    @Override
    public CallbackType type() {
        return CallbackType.PRESSURE_EXAMPLE_START;
    }

    @Override
    public String process(PressureStartNotifyParam param) {
        processStartSuccess(param);
        return String.valueOf(param.getData().getResourceId());
    }

    private void processStartSuccess(PressureStartNotifyParam context) {
        PressureExample data = context.getData();
        String resourceId = String.valueOf(data.getResourceId());
        String podId = String.valueOf(data.getPressureExampleId());
        ResourceContext resourceContext = getResourceContext(resourceId);
        if (resourceContext == null) {
            return;
        }
        if (!redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
            redisClientUtil.setSetValue(PressureStartCache.getResourceJmeterSuccessKey(resourceId), podId);
            Long tenantId = resourceContext.getTenantId();
            Long sceneId = resourceContext.getSceneId();
            Long reportId = resourceContext.getReportId();
            String engineName = ScheduleConstants.getEngineName(sceneId, reportId, tenantId);
            setMin(engineName + ScheduleConstants.FIRST_SIGN, context.getTime().getTime());
            if (Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(PressureStartCache.getJmeterStartFirstKey(resourceId), podId))) {
                cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
                    .checkEnum(SceneManageStatusEnum.RESOURCE_LOCKING,SceneManageStatusEnum.PRESSURE_NODE_RUNNING,SceneManageStatusEnum.STARTING)
                    .updateEnum(SceneManageStatusEnum.ENGINE_RUNNING)
                    .build());
                notifyStart(resourceContext, context);
                cacheTryRunTaskStatus(resourceContext);
            }
        }
    }

    private void cacheTryRunTaskStatus(ResourceContext context) {
        Long sceneId = context.getSceneId();
        Long reportId = context.getReportId();
        taskStatusCache.cacheStatus(sceneId, reportId, SceneRunTaskStatusEnum.RUNNING);
    }

    private void notifyStart(ResourceContext context, PressureStartNotifyParam param) {
        // 添加心跳数据
        Long reportId = context.getReportId();
        long startTime = param.getTime().getTime();
        String resourceId = context.getResourceId();
        redisClientUtil.hmset(PressureStartCache.getJmeterHeartbeatKey(resourceId),
            String.valueOf(param.getData().getPressureExampleId()), System.currentTimeMillis());
        cloudAsyncService.checkJmeterHeartbeatTask(context);
        cloudAsyncService.pressureStop(context.getPtTestTime(), resourceId, context.getJobId());
        log.info("场景[{}]压测任务开始，更新报告[{}]开始时间[{}]", context.getSceneId(), reportId, startTime);
        reportDao.updateReportStartTime(reportId, new Date(startTime));
    }
}
