package io.shulie.takin.cloud.biz.collector.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.adapter.api.entrypoint.pressure.PressureTaskApi;
import io.shulie.takin.adapter.api.entrypoint.resource.CloudResourceApi;
import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.pressure.PressureTaskStopReq;
import io.shulie.takin.adapter.api.model.request.resource.ResourceUnLockRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanResourceRequest;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanNode;
import io.shulie.takin.cloud.biz.notify.StartFailEventSource;
import io.shulie.takin.cloud.biz.notify.StopEventSource;
import io.shulie.takin.cloud.biz.notify.processor.calibration.PressureDataCalibration;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.PressureTaskStateEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.dao.scene.task.PressureTaskDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigSceneRelateShipMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.CollectionUtils;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 21:08
 */
@Slf4j
public abstract class AbstractIndicators {

    /**
     * 1、判断key是否存在，不存在插入value
     * 2、key存在，比较值大小
     */
    private static final String MAX_SCRIPT =
        "if (redis.call('exists', KEYS[1]) == 0 or redis.call('get', KEYS[1]) < ARGV[1]) then\n" +
            "    redis.call('set', KEYS[1], ARGV[1]);\n" +
            //            "    return 1;\n" +
            "else\n" +
            //            "    return 0;\n" +
            "end";
    private static final String MIN_SCRIPT =
        "if (redis.call('exists', KEYS[1]) == 0 or redis.call('get', KEYS[1]) > ARGV[1]) then\n" +
            "    redis.call('set', KEYS[1], ARGV[1]);\n" +
            //            "    return 1;\n" +
            "else\n" +
            //            "    return 0;\n" +
            "end";
    private static final String UNLOCK_SCRIPT = "if redis.call('exists',KEYS[1]) == 1 then\n" +
        "   redis.call('del',KEYS[1])\n" +
        "else\n" +
        //                    "   return 0\n" +
        "end";
    @Autowired
    protected CloudSceneManageService cloudSceneManageService;
    @Autowired
    protected EventCenterTemplate eventCenterTemplate;
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private ReportDao reportDao;
    @Resource
    private PressureTaskDAO pressureTaskDAO;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PressureTaskApi pressureTaskApi;
    @Resource
    private CloudResourceApi cloudResourceApi;
    @Resource
    private CloudWatchmanApi cloudWatchmanApi;
    @Resource
    private SceneManageDAO sceneManageDAO;
    @Resource
    private PressureDataCalibration pressureDataCalibration;
    @Resource
    private InterfacePerformanceConfigSceneRelateShipMapper interfacePerformanceConfigSceneRelateShipMapper;
    private DefaultRedisScript<Void> minRedisScript;
    private DefaultRedisScript<Void> maxRedisScript;
    private DefaultRedisScript<Void> unlockRedisScript;

    private static final int REDIS_KEY_TIMEOUT = 60;

    private final Expiration expiration = Expiration.seconds(REDIS_KEY_TIMEOUT);

    /**
     * 获取Metrics key
     * 示例：COLLECTOR:TASK:102121:213124512312
     *
     * @param sceneId  场景主键
     * @param reportId 报告主键
     * @return -
     */
    protected String getPressureTaskKey(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (tenantId == null) {
            return String.format("COLLECTOR:TASK:%s:%s", sceneId, reportId);
        }
        return String.format("COLLECTOR:TASK:%s:%s:%S", sceneId, reportId, tenantId);
    }

    public Boolean lock(String key, String value) {
        return redisTemplate.execute((RedisCallback<Boolean>)connection -> {
            Boolean bl = connection.set(getLockPrefix(key).getBytes(), value.getBytes(), expiration,
                RedisStringCommands.SetOption.SET_IF_ABSENT);
            return null != bl && bl;
        });
    }

    public void unlock(String key, String value) {
        redisTemplate.execute(unlockRedisScript, Lists.newArrayList(getLockPrefix(key)), value);
    }

    private String getLockPrefix(String key) {
        return String.format("COLLECTOR LOCK:%s", key);
    }

    /**
     * 获取Metrics 指标key
     * 示例：COLLECTOR:TASK:102121:213124512312:1587375600000:rt
     *
     * @param indicatorsName 指标名称
     * @return -
     */
    protected String getIndicatorsKey(String windowKey, String indicatorsName) {
        return String.format("%s:%s", windowKey, indicatorsName);
    }

    /**
     * time 不进行转换
     *
     * @param taskKey 任务key
     * @return -
     */
    protected String last(String taskKey) {
        return getIndicatorsKey(String.format("%s:%s", taskKey, "last"), "last");
    }

    protected void setLast(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    protected void setError(String key, String timestampPodNum, String value) {
        redisTemplate.opsForHash().put(key, timestampPodNum, value);
        setTtl(key);
    }

    protected void setMax(String key, Long value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            long temp = getEventTimeStrap(key);
            if (value > temp) {
                redisTemplate.opsForValue().set(key, value);
            }
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    protected void setMin(String key, Long value) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            long temp = getEventTimeStrap(key);
            if (value < temp) {
                redisTemplate.opsForValue().set(key, value);
            }
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    private void setTtl(String key) {
        redisTemplate.expire(key, REDIS_KEY_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 获取时间搓，取time 求min max
     *
     * @param key key
     * @return -
     */
    protected Long getEventTimeStrap(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        if (null != object) {
            return (long)object;
        }
        return null;
    }

    @PostConstruct
    public void init() {
        minRedisScript = new DefaultRedisScript<>();
        minRedisScript.setResultType(Void.class);
        minRedisScript.setScriptText(MIN_SCRIPT);

        maxRedisScript = new DefaultRedisScript<>();
        maxRedisScript.setResultType(Void.class);
        maxRedisScript.setScriptText(MAX_SCRIPT);

        unlockRedisScript = new DefaultRedisScript<>();
        unlockRedisScript.setResultType(Void.class);
        unlockRedisScript.setScriptText(UNLOCK_SCRIPT);

    }

    protected ResourceContext getResourceContext(String resourceId) {
        String resourceKey = PressureStartCache.getResourceKey(resourceId);
        Map<Object, Object> resource = redisClientUtil.hmget(resourceKey);
        if (CollectionUtils.isEmpty(resource)) {
            return null;
        }
        ResourceContext context = new ResourceContext();
        context.setResourceId(resourceId);
        context.setSceneId(Long.valueOf(String.valueOf(resource.get(PressureStartCache.SCENE_ID))));
        context.setReportId(Long.valueOf(String.valueOf(resource.get(PressureStartCache.REPORT_ID))));
        context.setTenantId(Long.valueOf(String.valueOf(resource.get(PressureStartCache.TENANT_ID))));
        context.setEnvCode(String.valueOf(resource.get(PressureStartCache.ENV_CODE)));
        context.setCheckStatus(String.valueOf(resource.get(PressureStartCache.CHECK_STATUS)));
        context.setTaskId(Long.valueOf(String.valueOf(resource.get(PressureStartCache.TASK_ID))));
        context.setUniqueKey(String.valueOf(resource.get(PressureStartCache.UNIQUE_KEY)));
        context.setPtTestTime(Long.valueOf(String.valueOf(resource.get(PressureStartCache.PT_TEST_TIME))));
        context.setPressureType(Integer.valueOf(String.valueOf(resource.get(PressureStartCache.PRESSURE_TYPE))));
        context.setAttachId(String.valueOf(resource.get(PressureStartCache.FILE_ATTACH_ID)));
        context.setMachineId(String.valueOf(resource.get(PressureStartCache.MACHINE_ID)));
        Object machineType = resource.get(PressureStartCache.MACHINE_TYPE);
        if (Objects.nonNull(machineType)) {
            context.setMachineType(Integer.valueOf(String.valueOf(machineType)));
        }
        Object jobId = resource.get(PressureStartCache.JOB_ID);
        if (Objects.nonNull(jobId)) {
            context.setJobId(Long.valueOf(String.valueOf(jobId)));
        }
        return context;
    }

    protected void callStartFailedEvent(String resourceId, String message) {
        ResourceContext context = getResourceContext(resourceId);
        if (context != null) {
            Event event = new Event();
            if (Objects.equals(context.getCheckStatus(), String.valueOf(CheckStatus.SUCCESS.ordinal()))) {
                String stopTaskMessageKey = PressureStartCache.getStopTaskMessageKey(context.getSceneId());
                String stopMessage = redisClientUtil.getString(stopTaskMessageKey);
                if (StringUtils.isNotBlank(stopMessage)) {
                    redisClientUtil.del(stopTaskMessageKey);
                    message = stopMessage;
                }
                event.setEventName(PressureStartCache.START_FAILED);
                event.setExt(new StartFailEventSource(context, message));
            } else {
                context.setMessage(message);
                event.setEventName(PressureStartCache.CHECK_FAIL_EVENT);
                event.setExt(context);
            }
            eventCenterTemplate.doEvents(event);
        }
    }

    protected void callRunningFailedEvent(String resourceId, String message) {
        callRunningFailedEvent(resourceId, message, false);
    }

    protected void callRunningFailedEvent(String resourceId, String message, boolean interrupt) {
        ResourceContext context = getResourceContext(resourceId);
        if (context != null) {
            Event event = new Event();
            context.setMessage(message);
            if (Objects.equals(context.getCheckStatus(), String.valueOf(CheckStatus.SUCCESS.ordinal()))) {
                if (redisClientUtil.hasKey(PressureStartCache.getJmeterStartFirstKey(resourceId))) {
                    event.setEventName(PressureStartCache.RUNNING_FAILED);
                    event.setExt(new StopEventSource(context, message, interrupt));
                } else {
                    event.setEventName(PressureStartCache.START_FAILED);
                    event.setExt(new StartFailEventSource(context, message));
                }
            } else {
                event.setEventName(PressureStartCache.CHECK_FAIL_EVENT);
                event.setExt(context);
            }
            eventCenterTemplate.doEvents(event);
        }
    }

    protected void removeSuccessKey(String resourceId, String podId, String jmeterId, Date time) {
        Long podCount = redisClientUtil.remSetValueAndReturnCount(
            PressureStartCache.getResourcePodSuccessKey(resourceId), podId);
        Long jmeterCount = -1L;
        if (StringUtils.isNotBlank(jmeterId)) {
            jmeterCount = redisClientUtil.remSetValueAndReturnCount(
                PressureStartCache.getResourceJmeterSuccessKey(resourceId), jmeterId);
        }
        if (podCount == 0 || jmeterCount == 0) {
            detectEnd(resourceId, time);
        }
    }

    protected void detectEnd(String resourceId, Date time) {
        ResourceContext context = getResourceContext(resourceId);
        Long sceneId = context.getSceneId();
        Long reportId = context.getReportId();
        Long tenantId = context.getTenantId();
        String engineName = ScheduleConstants.getEngineName(sceneId, reportId, tenantId);
        setMax(engineName + ScheduleConstants.LAST_SIGN, time.getTime());
        if (Objects.equals(context.getCheckStatus(), String.valueOf(CheckStatus.SUCCESS.ordinal()))
            && redisClientUtil.lockExpire(PressureStartCache.getFinishStopKey(resourceId),
            String.valueOf(System.currentTimeMillis()), 10, TimeUnit.MINUTES)) {
            setLast(last(getPressureTaskKey(sceneId, reportId, tenantId)), ScheduleConstants.LAST_SIGN);
            // 压测停止
            notifyEnd(context, time);
        }
    }

    protected void notifyFinish(ResourceContext context) {
        String resourceId = context.getResourceId();
        // 清除 SLA配置  生成报告拦截 状态拦截
        if (!Objects.equals(context.getPressureType(), PressureSceneEnum.INSPECTION_MODE.getCode())
            && redisClientUtil.lockExpire(PressureStartCache.getResourceFinishEventKey(resourceId),
            String.valueOf(System.currentTimeMillis()), 10, TimeUnit.MINUTES)) {
            Event event = new Event();
            event.setEventName("finished");
            TaskResult result = new TaskResult(context.getSceneId(), context.getReportId(), context.getTenantId());
            result.setResourceId(context.getResourceId());
            event.setExt(result);
            eventCenterTemplate.doEvents(event);
            log.info("成功发送finished事件,resourceId={}", resourceId);
        } else {
            log.warn("未发送finished事件,resourceId={}", resourceId);
        }
    }

    protected void notifyEnd(ResourceContext context, Date time) {
        String now = String.valueOf(System.currentTimeMillis());
        if (redisClientUtil.lockExpire(
            PressureStartCache.getStopSuccessFlag(context.getResourceId()), now, 10, TimeUnit.MINUTES)) {
            Long sceneId = context.getSceneId();
            Long reportId = context.getReportId();
            Long tenantId = context.getTenantId();
            log.info("场景[{}-{}]压测任务已完成,更新结束时间{}", sceneId, reportId, System.currentTimeMillis());
            // 更新压测场景状态  压测引擎运行中,压测引擎停止压测 ---->压测引擎停止压测
            cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(sceneId, reportId, tenantId)
                .checkEnum(SceneManageStatusEnum.STARTING,
                    SceneManageStatusEnum.JOB_CREATING, SceneManageStatusEnum.PRESSURE_NODE_RUNNING,
                    SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.STOP)
                .updateEnum(SceneManageStatusEnum.STOP).build());
            updateReportEndTime(context, time);
            pressureTaskDAO.updateStatus(context.getTaskId(), PressureTaskStateEnum.INACTIVE, null);
        }
    }

    protected void updateReportStartTime(ResourceContext context) {
        Long reportId = context.getReportId();
        String engineName = ScheduleConstants.getEngineName(context.getSceneId(), reportId, context.getTenantId());
        String startTimeKey = engineName + ScheduleConstants.FIRST_SIGN;
        String dateTimeString = stringRedisTemplate.opsForValue().get(startTimeKey);
        if (StringUtils.isNotBlank(dateTimeString)) {
            reportDao.updateReportStartTime(reportId, (new Date(Long.parseLong(dateTimeString))));
            stringRedisTemplate.delete(startTimeKey);
        }
    }

    protected void updateReportEndTime(ResourceContext context, Date time) {
        Long reportId = context.getReportId();
        String engineName = ScheduleConstants.getEngineName(context.getSceneId(), reportId, context.getTenantId());
        String endTimeKey = engineName + ScheduleConstants.LAST_SIGN;
        String dateTimeString = stringRedisTemplate.opsForValue().get(endTimeKey);
        if (StringUtils.isNotBlank(dateTimeString)) {
            time = new Date(Long.parseLong(dateTimeString));
        }
        reportDao.updateReportEndTime(reportId, time);
    }

    protected void notifyStop(ResourceContext context) {
        pressureTaskDAO.updateStatus(context.getTaskId(), PressureTaskStateEnum.STOPPING, null);
    }

    protected void generateReport(Long taskId) {
        if (redisClientUtil.lockExpire(PressureStartCache.getReportGenerateKey(taskId),
            String.valueOf(System.currentTimeMillis()), 5, TimeUnit.MINUTES)) {
            pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.REPORT_GENERATING, null);
        }
    }

    protected void doneReport(ReportResult report) {
        Long taskId = report.getTaskId();
        if (redisClientUtil.lockExpire(PressureStartCache.getReportDoneKey(taskId),
            String.valueOf(System.currentTimeMillis()), 5, TimeUnit.MINUTES)) {
            pressureTaskDAO.updateStatus(taskId, PressureTaskStateEnum.REPORT_DONE, null);
            dataCalibration(report);
        }
    }

    private void dataCalibration(ReportResult report) {
        Long jobId = report.getJobId();
        String resourceId = report.getResourceId();
        if (redisClientUtil.hasKey(PressureStartCache.getJmeterStartFirstKey(resourceId))
            && redisClientUtil.lockExpire(PressureStartCache.getDataCalibrationLockKey(jobId),
            String.valueOf(report.getId()), 5, TimeUnit.MINUTES)) {
            TaskResult result = new TaskResult();
            result.setSceneId(report.getSceneId());
            result.setTaskId(jobId);
            result.setResourceId(resourceId);
            WebPluginUtils.fillCloudUserData(result);
            dataCalibration(result);
            pressureDataCalibration.dataCalibrationAmdb(result);
            pressureDataCalibration.dataCalibrationCloud(result);
        }
    }

    protected void pressureEnd(ReportResult reportResult) {
        Event event = new Event();
        event.setEventName(PressureStartCache.PRESSURE_END);
        ResourceContext context = new ResourceContext();
        context.setSceneId(reportResult.getSceneId());
        context.setResourceId(reportResult.getResourceId());
        context.setTaskId(reportResult.getTaskId());
        context.setJobId(reportResult.getJobId());
        context.setTenantId(reportResult.getTenantId());
        context.setReportId(reportResult.getId());
        event.setExt(context);
        eventCenterTemplate.doEvents(event);
    }

    protected void stopJob(String resourceId, Long jobId) {
        if (redisClientUtil.hasKey(PressureStartCache.getJmeterStartFirstKey(resourceId))) {
            PressureTaskStopReq request = new PressureTaskStopReq();
            request.setPressureId(jobId);
            pressureTaskApi.stop(request);
        } else {
            releaseResource(resourceId);
        }
    }

    /**
     * 释放资源
     *
     * @param resourceId 资源Id
     */
    protected void releaseResource(String resourceId) {
        if (StringUtils.isNotBlank(resourceId)) {
            ResourceUnLockRequest request = new ResourceUnLockRequest();
            request.setResourceId(resourceId);
            cloudResourceApi.unLock(request);
        }
    }

    protected void updatePressureTaskMessageByResourceId(String resourceId, String message) {
        pressureTaskDAO.updatePressureTaskMessageByResourceId(resourceId, message);
    }

    private void dataCalibration(TaskResult result) {
        String statusKey = PressureStartCache.getDataCalibrationStatusKey(result.getTaskId());
        redisClientUtil.setBit(statusKey, PressureDataCalibration.offset(false) + 1, true);
        redisClientUtil.setBit(statusKey, PressureDataCalibration.offset(true) + 1, true);
    }

    protected void updateSceneMachineId(Long sceneId, String machineId, Integer machineType) {
        if (Objects.isNull(sceneId) || StringUtils.isBlank(machineId)) {
            return;
        }
        SceneManageEntity sceneById = sceneManageDAO.getSceneById(sceneId);
        String features = sceneById.getFeatures();
        JSONObject param;
        if (StringUtils.isNotEmpty(features)) {
            param = JSONObject.parseObject(features);
        } else {
            param = new JSONObject();
        }
        param.put(PressureStartCache.FEATURES_MACHINE_ID, machineId);
        param.put(PressureStartCache.FEATURES_MACHINE_TYPE, machineType);
        String machineFeatures = param.toJSONString();
        sceneManageDAO.getBaseMapper().update(null,
            Wrappers.lambdaUpdate(SceneManageEntity.class)
                .set(SceneManageEntity::getFeatures, machineFeatures)
                .eq(SceneManageEntity::getId, sceneId));

        // 关联更新单接口压测
        interfacePerformanceConfigSceneRelateShipMapper.update(null,
            Wrappers.lambdaUpdate(InterfacePerformanceConfigSceneRelateShipEntity.class)
            .set(InterfacePerformanceConfigSceneRelateShipEntity::getFeatures, machineFeatures)
            .eq(InterfacePerformanceConfigSceneRelateShipEntity::getSceneId, sceneId)
            .eq(InterfacePerformanceConfigSceneRelateShipEntity::getIsDeleted, 0));
    }

    protected void updateReportPtlLocation(ResourceContext ext) {
        try {
            String machineId = ext.getMachineId();
            String relativePath = DataUtils.mergePath(ext.getResourceId(), String.valueOf(ext.getJobId()), "/");
            WatchmanResourceRequest request = new WatchmanResourceRequest();
            request.setWatchmanId(machineId);
            List<WatchmanNode> resource = cloudWatchmanApi.resource(request);
            resource.stream().filter(node -> !StringUtils.isAllBlank(node.getNfsServer(), node.getNfsDir()))
                .findAny().ifPresent(node -> updatePtlPath(ext.getReportId(), node, relativePath));
        } catch (Exception ignore) {}
    }

    private void updatePtlPath(Long reportId, WatchmanNode node, String relativePath) {
        ReportResult reportResult = reportDao.getById(reportId);
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(reportResult.getFeatures())) {
            map = JsonHelper.string2Obj(reportResult.getFeatures(), new TypeReference<Map<String, String>>() {
            });
        }
        String nfsServer = node.getNfsServer();
        String nfsDir = node.getNfsDir();
        map.put(PressureStartCache.FEATURES_NFS_SERVER, nfsServer);
        map.put(PressureStartCache.FEATURES_NFS_ROOT, nfsDir);
        map.put(PressureStartCache.FEATURES_PTL_LOG_SERVER, node.getPtlLogServer());
        if (Objects.equals(map.get(PressureStartCache.FEATURES_MACHINE_TYPE), String.valueOf(EngineType.PRIVATE.getType()))) {
            List<String> paths = new ArrayList<>(3);
            paths.add("nfsServer：" + nfsServer);
            paths.add("ptl：" +  DataUtils.mergePath(DataUtils.mergePath(nfsDir, "ptl", "/"), relativePath, "/"));
            paths.add("logs：" +  DataUtils.mergePath(DataUtils.mergePath(nfsDir, "logs", "/"), relativePath, "/"));
            map.put(PressureStartCache.FEATURES_MACHINE_PTL_PATH, StringUtils.join(paths, ","));
        }
        ReportUpdateParam param = new ReportUpdateParam();
        param.setId(reportId);
        param.setFeatures(GsonUtil.gsonToString(map));
        reportDao.updateReport(param);
    }

    @Data
    public static class ResourceContext {
        private Long sceneId;
        private Long reportId;
        private Long taskId;
        private String envCode;
        private Long jobId;
        private String resourceId;
        private Long tenantId;
        private Long podNumber;
        private String checkStatus;
        private String uniqueKey;
        private Long ptTestTime;
        private Integer pressureType;
        private String machineId;
        private Integer machineType;
        private String attachId;
        private String message;
        private boolean fileFailed;
    }
}
