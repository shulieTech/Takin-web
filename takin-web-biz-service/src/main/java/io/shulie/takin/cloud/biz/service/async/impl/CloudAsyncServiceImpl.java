package io.shulie.takin.cloud.biz.service.async.impl;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.notify.StartFailEventSource;
import io.shulie.takin.cloud.biz.service.async.CloudAsyncService;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.web.biz.checker.StartConditionCheckerContext;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author qianshui
 * @date 2020/10/30 下午7:13
 */
@Service
@Slf4j
public class CloudAsyncServiceImpl extends AbstractIndicators implements CloudAsyncService {

    @Resource
    private EventCenterTemplate eventCenterTemplate;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private PressureTaskDAO pressureTaskDAO;
    @Resource(name = "pressureStopPool")
    private ScheduledExecutorService pressureStopExecutor;

    /**
     * 压力节点 启动时间超时
     */
    @Value("${pressure.pod.start.expireTime: 30}")
    private Integer pressurePodStartExpireTime;

    /**
     * 压力引擎 启动时间超时
     */
    @Value("${pressure.node.start.expireTime: 30}")
    private Integer pressureNodeStartExpireTime;

    /**
     * 压力引擎 心跳时间超时
     */
    @Value("${pressure.node.heartbeat.expireTime: 30}")
    private Integer pressureNodeHeartbeatExpireTime;

    /**
     * 压测启动超时时间 单位分钟 [ 调用了task/start接口后，多久没有调用到cloud的start接口触发失败 ]
     */
    @Value("${pressure.start.expireTime: 3}")
    private Integer startTimeout;

    @Resource(name = "schedulerPool")
    private TaskScheduler taskScheduler;

    /**
     * 线程定时检查休眠时间
     */
    private final static Integer CHECK_INTERVAL_TIME = 3;

    @Async("checkStartedPodPool")
    @Override
    public void checkPodStartedTask(StartConditionCheckerContext context) {
        String resourceId = context.getResourceId();
        log.info("启动[{}]后台检查pod启动状态线程.....", resourceId);
        int currentTime = 0;
        boolean checkPass = false;
        Object totalPodNumber = redisClientUtil.hmget(PressureStartCache.getResourceKey(resourceId),
            PressureStartCache.POD_NUM);
        if (Objects.isNull(totalPodNumber)) {
            return;
        }
        String podNumber = String.valueOf(totalPodNumber);
        while (currentTime <= pressurePodStartExpireTime
            && !redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
            Long startedPod = redisClientUtil.getSetSize(PressureStartCache.getResourcePodSuccessKey(resourceId));
            try {
                if (Long.parseLong(podNumber) == startedPod) {
                    checkPass = true;
                    log.info("后台检查到pod全部启动成功.....");
                    break;
                }
            } catch (Exception e) {
                log.error("异常代码【{}】,异常内容：任务启动异常 --> 从Redis里获取节点数量数据格式异常: {}",
                    TakinCloudExceptionEnum.TASK_START_ERROR_CHECK_POD, e);
            }
            try {
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_TIME);
            } catch (InterruptedException ignore) {
            }
            currentTime += CHECK_INTERVAL_TIME;
        }
        //压力pod没有在设定时间内启动完毕，停止检测
        markResourceStatus(checkPass, context);
    }

    @Async("checkStartedJmeterPool")
    @Override
    public void checkJmeterStartedTask(ResourceContext context) {
        String resourceId = context.getResourceId();
        log.info("启动[{}]后台检查jmeter启动状态线程.....", resourceId);
        int currentTime = 0;
        boolean checkPass = false;
        String podNumber = String.valueOf(redisClientUtil.hmget(PressureStartCache.getResourceKey(resourceId),
            PressureStartCache.POD_NUM));
        long startedPod = 0;
        while (currentTime <= pressureNodeStartExpireTime
            && !redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
            startedPod = redisClientUtil.getSetSize(PressureStartCache.getResourceJmeterSuccessKey(resourceId));
            try {
                if (Long.parseLong(podNumber) == startedPod) {
                    checkPass = true;
                    log.info("后台检查到jmeter全部启动成功.....");
                    break;
                }
            } catch (Exception e) {
                log.error("异常代码【{}】,异常内容：任务启动异常 --> 从Redis里获取节点数量数据格式异常: {}",
                    TakinCloudExceptionEnum.TASK_START_ERROR_CHECK_POD, e);
            }
            try {
                TimeUnit.SECONDS.sleep(CHECK_INTERVAL_TIME);
            } catch (InterruptedException ignore) {
            }
            currentTime += CHECK_INTERVAL_TIME;
        }
        //压力jmeter没有在设定时间内启动完毕，停止检测
        if (!checkPass) {
            String message = String.format("节点没有在设定时间【%s】s内启动，计划启动节点个数【%s】,实际启动节点个数【%s】,"
                    + "导致压测停止", pressureNodeStartExpireTime, podNumber, startedPod);
            callStartFailedEvent(resourceId, message);
        } else {
            // 启动完成
            markJmeterStarted(context);
        }
    }

    @Async("checkJmeterHeartbeatPool")
    @Override
    public void checkJmeterHeartbeatTask(ResourceContext context) {
        String resourceId = context.getResourceId();
        if (Objects.equals(PressureSceneEnum.INSPECTION_MODE.getCode(), context.getPressureType())) {
            // 如果是巡检任务，不进行心跳检测
            log.info("巡检任务[{} - {} - {}]不启动jmeter心跳检测", context.getSceneId(), resourceId, context.getJobId());
            return;
        }
        log.info("启动[{}]后台检查jmeter心跳状态线程.....", resourceId);
        int checkTime = pressureNodeHeartbeatExpireTime * 1000;
        while (!redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
            Map<Object, Object> heartbeatMap = redisClientUtil.hmget(PressureStartCache.getJmeterHeartbeatKey(resourceId));
            if (CollectionUtils.isEmpty(heartbeatMap)) {
                callRunningFailedEvent(resourceId, "jmeter节点超时未上报心跳数据");
                return;
            }
            for (Entry<Object, Object> entry : heartbeatMap.entrySet()) {
                if (Long.parseLong(String.valueOf(entry.getValue())) + checkTime < System.currentTimeMillis()) {
                    callRunningFailedEvent(resourceId, String.format("jmeter节点[%s]心跳超时", entry.getKey()));
                    return;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(checkTime);
            } catch (InterruptedException ignore) {
            }
        }
    }

    // @Async("checkPodHeartbeatPool")
    @Override
    public void checkPodHeartbeatTask(ResourceContext context) {
        String resourceId = context.getResourceId();
        log.info("启动[{}]后台检查pod心跳状态线程.....", resourceId);
        int checkTime = pressureNodeHeartbeatExpireTime * 1000;
        while (!redisClientUtil.hasLockKey(PressureStartCache.getStopFlag(resourceId))) {
            Map<Object, Object> heartbeatMap = redisClientUtil.hmget(PressureStartCache.getPodHeartbeatKey(resourceId));
            if (CollectionUtils.isEmpty(heartbeatMap)) {
                callRunningFailedEvent(resourceId, "pod节点超时未上报心跳数据");
                return;
            }
            for (Entry<Object, Object> entry : heartbeatMap.entrySet()) {
                if (Long.parseLong(String.valueOf(entry.getValue())) + checkTime < System.currentTimeMillis()) {
                    callRunningFailedEvent(resourceId, String.format("pod节点[%s]心跳超时", entry.getKey()));
                    return;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(checkTime);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public void checkStartTimeout(Long sceneId) {
        if (Objects.nonNull(sceneId)) {
            // 先拿出resourceId
            Object resource = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId), PressureStartCache.RESOURCE_ID);
            if (Objects.nonNull(resource)) {
                String resourceId = String.valueOf(resource);
                taskScheduler.schedule(() -> {
                    ResourceContext context = getResourceContext(resourceId);
                    // 还未被取消且没有job数据
                    if (Objects.nonNull(context) && Objects.isNull(context.getJobId())) {
                        Event event = new Event();
                        StartFailEventSource source = new StartFailEventSource();
                        source.setContext(context);
                        source.setMessage("压测启动超时");
                        event.setEventName(PressureStartCache.START_FAILED);
                        event.setExt(source);
                        eventCenterTemplate.doEvents(event);
                    }
                }, new Date(System.currentTimeMillis() + startTimeout * 60 * 1000));
            }
        }
    }

    @Override
    public void pressureStop(Long delay, String resourceId, Long jobId) {
        pressureStopExecutor.schedule(() -> stopJob(resourceId, jobId), delay, TimeUnit.SECONDS);
    }

    private void markResourceStatus(boolean success, StartConditionCheckerContext context) {
        String resourceId = context.getResourceId();
        ResourceContext resourceContext = getResourceContext(resourceId);
        resourceContext.setUniqueKey(context.getUniqueKey());
        if (success) {
            Event event = new Event();
            event.setEventName(PressureStartCache.CHECK_SUCCESS_EVENT);
            event.setExt(resourceContext);
            eventCenterTemplate.doEvents(event);
        } else {
            log.info("调度任务{}-{}-{},压力节点 没有在设定时间{}s内启动，停止压测,",
                context.getSceneId(), context.getReportId(), context.getTenantId(), pressurePodStartExpireTime);
            resourceContext.setMessage("压力机资源不足");
            Event event = new Event();
            event.setEventName(PressureStartCache.CHECK_FAIL_EVENT);
            event.setExt(resourceContext);
            eventCenterTemplate.doEvents(event);
        }
    }

    private void markJmeterStarted(ResourceContext context) {
        Long taskId = context.getTaskId();
        pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.ALIVE, null);
        pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.PRESSURING, null);
        updateReportStartTime(context);
    }
}
