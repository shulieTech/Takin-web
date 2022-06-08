package io.shulie.takin.cloud.data.util;

import java.util.Arrays;
import java.util.List;

import io.shulie.takin.web.common.util.RedisClientUtil;

public abstract class PressureStartCache {

    // 缓存
    public static final String CHECK_STATUS = "check_status";
    public static final String POD_NUM = "pod_num";
    public static final String TENANT_ID = "tenant_id";
    public static final String ENV_CODE = "env_code";
    public static final String SCENE_ID = "scene_id";
    public static final String REPORT_ID = "report_id";
    public static final String TASK_ID = "task_id";
    public static final String RESOURCE_ID = "resource_Id";
    public static final String UNIQUE_KEY = "unique_key";
    public static final String JOB_ID = "job_id";
    public static final String START_FLAG = "start_flag";
    public static final String USER_ID = "user_id";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String PT_TEST_TIME = "pt_test_time";
    public static final String PRESSURE_TYPE = "pressure_type";

    // 事件
    public static final String CHECK_FAIL_EVENT = "check_failed";
    public static final String CHECK_SUCCESS_EVENT = "check_success";
    public static final String PRE_STOP_EVENT = "pre_stop";
    public static final String UNLOCK_FLOW = "unlock_flow";
    public static final String START_FAILED = "start_fail";
    public static final String RUNNING_FAILED = "running_fail";
    public static final String PRESSURE_END = "pressure_end";

    public static String getResourceKey(String resourceId) {
        return String.format("pressure:resource:%s:locking", resourceId);
    }

    // 启动成功的pod实例名称存入该key
    public static String getResourcePodSuccessKey(String resourceId) {
        return String.format("pressure:resource:%s:pod:success", resourceId);
    }

    // 启动成功的jmeter实例名称存入该key,stop 时会移除
    public static String getResourceJmeterSuccessKey(String resourceId) {
        return String.format("pressure:resource:%s:jmeter:success", resourceId);
    }

    // 场景启动锁，保证场景不能同时启动多次
    public static String getSceneResourceLockingKey(Long sceneId) {
        return String.format("pressure:scene:%s:locking", sceneId);
    }

    // 场景Id为key保存的相关信息
    public static String getSceneResourceKey(Long sceneId) {
        return String.format("pressure:scene:%s:resource", sceneId);
    }

    // 场景提前取消压测标识
    public static String getScenePreStopKey(Long sceneId, String uniqueKey) {
        return String.format("pressure:scene:%s:%s:pre_stop", sceneId, uniqueKey);
    }

    // 压测启动标识
    public static String getStartFlag(String resourceId) {
        return String.format("pressure:resource:%s:start", resourceId);
    }

    // 压测停止标识：jmeter停止、jmeter异常、pod异常、jmeter启动超时、pod启动超时、jmeter心跳超时、pod心跳超时
    public static String getStopFlag(String resourceId) {
        return String.format("pressure:resource:%s:stop", resourceId);
    }

    public static String getStopSuccessFlag(String resourceId) {
        return String.format("pressure:resource:%s:stop_success", resourceId);
    }

    // jmeter心跳时间
    public static String getJmeterHeartbeatKey(String resourceId) {
        return String.format("pressure:resource:%s:jmeter:heartbeat", resourceId);
    }

    // pod心跳时间
    public static String getPodHeartbeatKey(String resourceId) {
        return String.format("pressure:resource:%s:pod:heartbeat", resourceId);
    }

    // 第一个启动的pod id
    public static String getPodStartFirstKey(String resourceId) {
        return String.format("pressure:resource:%s:pod:first:start", resourceId);
    }

    // 第一个启动的jmeter id
    public static String getJmeterStartFirstKey(String resourceId) {
        return String.format("pressure:resource:%s:jmeter:first:start", resourceId);
    }

    // 第一个异常的jmeter id
    public static String getPodErrorFirstKey(String resourceId) {
        return String.format("pressure:resource:%s:pod:first:error", resourceId);
    }

    // 第一个停止的pod id
    public static String getPodStopFirstKey(String resourceId) {
        return String.format("pressure:resource:%s:pod:first:stop", resourceId);
    }

    // 第一个异常的jmeter id
    public static String getJmeterErrorFirstKey(String resourceId) {
        return String.format("pressure:resource:%s:jmeter:first:error", resourceId);
    }

    public static String getFlowDebugKey(Long sceneId) {
        return String.format("pressure:scene:%s:flow_debug", sceneId);
    }

    public static String getInspectKey(Long sceneId) {
        return String.format("pressure:scene:%s:inspect", sceneId);
    }

    public static String getTryRunKey(Long sceneId) {
        return String.format("pressure:scene:%s:try_run", sceneId);
    }

    // 停止完成key
    public static String getFinishStopKey(String resourceId) {
        return String.format("pressure:resource:%s:stopped", resourceId);
    }

    // 释放流量key
    public static String getReleaseFlowKey(Long reportId) {
        return String.format("pressure:resource:%s:flow:unlock", reportId);
    }

    // 锁定流量key
    public static String getLockFlowKey(Long reportId) {
        return String.format("pressure:resource:%s:flow:lock", reportId);
    }

    public static String getStopTaskMessageKey(Long sceneId) {
        return String.format("pressure:scene:%s:stop_message", sceneId);
    }

    public static String getResourceCheckFailKey(String resourceId) {
        return String.format("pressure:resource:%s:check_fail", resourceId);
    }

    public static String getReportGenerateKey(Long taskId) {
        return String.format("pressure:scene:%s:report_generate", taskId);
    }

    public static String getReportDoneKey(Long taskId) {
        return String.format("pressure:scene:%s:report_done", taskId);
    }

    public static String getDataCalibrationLockKey(Long jobId) {
        return String.format("pressure:resource:%s:calibration", jobId);
    }

    public static String getDataCalibrationStatusKey(Long jobId) {
        return String.format("pressure:resource:%s:calibration_status", jobId);
    }

    public static String getDataCalibrationMessageKey(Long jobId) {
        return String.format("pressure:resource:%s:calibration_message", jobId);
    }

    public static String getDataCalibrationCallbackKey(Long jobId, String source) {
        return String.format("pressure:resource:%s:%s:calibration_callback", jobId, source);
    }

    public static String getReportCachedKey(Long reportId) {
        return String.format("pressure:resource:%s:cached", reportId);
    }

    public static String getResourceFinishEventKey(String resourceId) {
        return String.format("pressure:resource:%s:finish_event", resourceId);
    }

    public static List<String> clearCacheKey(String resourceId, Long sceneId) {
        return Arrays.asList(PressureStartCache.getResourceKey(resourceId),
            PressureStartCache.getResourcePodSuccessKey(resourceId),
            PressureStartCache.getPodHeartbeatKey(resourceId),
            PressureStartCache.getPodStartFirstKey(resourceId),
            PressureStartCache.getResourceJmeterSuccessKey(resourceId),
            PressureStartCache.getJmeterHeartbeatKey(resourceId),
            PressureStartCache.getJmeterStartFirstKey(resourceId),
            PressureStartCache.getSceneResourceLockingKey(sceneId),
            PressureStartCache.getSceneResourceKey(sceneId),
            PressureStartCache.getFlowDebugKey(sceneId),
            PressureStartCache.getInspectKey(sceneId),
            PressureStartCache.getTryRunKey(sceneId),
            RedisClientUtil.getLockKey(PressureStartCache.getStartFlag(resourceId)),
            RedisClientUtil.getLockKey(PressureStartCache.getStopSuccessFlag(resourceId)),
            RedisClientUtil.getLockKey(PressureStartCache.getPodStopFirstKey(resourceId)),
            RedisClientUtil.getLockKey(PressureStartCache.getJmeterErrorFirstKey(resourceId)),
            RedisClientUtil.getLockKey(PressureStartCache.getPodErrorFirstKey(resourceId)),
            RedisClientUtil.getLockKey(PressureStartCache.getFinishStopKey(resourceId)),
            RedisClientUtil.getLockKey(PressureStartCache.getSceneResourceLockingKey(sceneId)
            )
        );
    }
}
