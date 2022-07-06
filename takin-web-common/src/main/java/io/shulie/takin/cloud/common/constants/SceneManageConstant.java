package io.shulie.takin.cloud.common.constants;

import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author qianshui
 * @date 2020/4/18 上午11:49
 */
public class SceneManageConstant {

    public static final String FILE_SPLIT = "/";

    public static final String SCENE_TOPIC_PREFIX = "TRO_SCENE_";

    // ---- 施压配置

    public static final String THREAD_NUM = "threadNum";

    public static final String HOST_NUM = "hostNum";

    public static final String PT_DURATION = "ptDuration";

    public static final String PT_DURATION_UNIT = "ptDurationUnit";

    /**
     * 0:并发,1:tps,2:自定义
     */
    public static final String PT_TYPE = "ptType";

    public static final String PT_MODE = "ptMode";

    public static final String STEP_DURATION = "stepDuration";

    public static final String STEP_DURATION_UNIT = "stepDurationUnit";

    public static final String STEP = "step";

    public static final String ESTIMATE_FLOW = "estimateFlow";

    public static final String DATA_COUNT = "dataCount";

    public static final String IS_SPLIT = "isSplit";

    public static final String IS_BIG_FILE = "isBigFile";

    public static final String TOPIC = "topic";

    public static final String IS_ORDERED_SPLIT = "isOrderSplit";

    // ---- 业务活动目标

    public static final String TPS = "TPS";

    public static final String RT = "RT";

    public static final String SUCCESS_RATE = "successRate";

    public static final String SA = "SA";

    // ---- SLA配置

    public static final String COMPARE_TYPE = "compareType";

    public static final String COMPARE_VALUE = "compareValue";

    public static final String ACHIEVE_TIMES = "achieveTimes";

    public static final String EVENT = "event";

    public static final String EVENT_WARN = "warn";

    public static final String EVENT_DESTORY = "destory";

    // features ,扩展字段

    public static final String FEATURES_CONFIG_TYPE = "configType";

    public static final String FEATURES_SCRIPT_ID = "scriptId";

    public static final String FEATURES_BUSINESS_FLOW_ID = "businessFlowId";

    public static final String FEATURES_SCHEDULE_INTERVAL = "scheduleInterval";

    //压测场景模拟业务流量启动场景

    public static final String SCENE_MANAGER_FLOW_DEBUG = "SCENE_MANAGER_FLOW_DEBUG_";

    public static final String SCENE_MANAGER_INSPECT = "SCENE_MANAGER_INSPECT_";

    /**
     * 流量试跑
     */
    public static final String SCENE_MANAGER_TRY_RUN = "SCENE_MANAGER_TRY_RUN_";

    // ---- 压测任务状态
    /**
     * 压测任务状态
     * <p>job不存在</p>
     */
    public static String SCENE_TASK_JOB_STATUS_NONE = "none";
    /**
     * 压测任务状态
     * <p>运行中</p>
     */
    public static String SCENE_TASK_JOB_STATUS_RUNNING = "running";

    public static String getTryRunSceneName(Long scriptDeployId) {
        return SCENE_MANAGER_TRY_RUN + WebPluginUtils.traceTenantId() + "_" + scriptDeployId;
    }

    public static String getFlowDebugSceneName(Long scriptId) {
        return SCENE_MANAGER_FLOW_DEBUG + WebPluginUtils.traceTenantId() + "_" + scriptId;

    }

    public static String getInspectSceneName(Long scriptId) {
        return SCENE_MANAGER_INSPECT + WebPluginUtils.traceTenantId() + "_" + scriptId;
    }
}
