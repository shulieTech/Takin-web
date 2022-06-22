package io.shulie.takin.cloud.common.constants;

/**
 * @author 莫问
 * @date 2020-05-21
 */

public class ScheduleConstants {

    /**
     * 调度状态：成功
     */
    public static final int SCHEDULE_STATUS_1 = 1;

    public static String FIRST_SIGN = "-first";
    public static String LAST_SIGN = "-last";

    public static String SCHEDULE_POD_NUM = "schedule-pressure-node-num";

    /**
     * 调度任务job
     */
    public static String SCENE_TASK = "scene-task-";

    /**
     * 文件分割存储的队列
     */
    public static String getFileSplitQueue(Long scenId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("file-split-queue-%s-%s", scenId, reportId);
        }
        return String.format("file-split-queue-%s-%s-%s", scenId, reportId, tenantId);
    }

    /**
     * 调度名称
     *
     * @return -
     */
    public static String getScheduleName(Long sceneId, Long taskId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format(SCENE_TASK + "%s-%s", sceneId, taskId);
        }
        return String.format(SCENE_TASK + "%s-%s-%s", sceneId, taskId, tenantId);
    }

    /**
     * 压力节点 引擎名
     *
     * @return -
     */
    public static String getEngineName(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("pressure-node-engine-%s-%s", sceneId, reportId);
        }
        return String.format("pressure-node-engine-%s-%s-%s", sceneId, reportId, tenantId);
    }

    /**
     * 压力节点名总数名称
     *
     * @return -
     */
    public static String getPressureNodeTotalKey(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("pressure-node-total-%s-%s", sceneId, reportId);
        }
        return String.format("pressure-node-total-%s-%s-%s", sceneId, reportId, tenantId);
    }

    /**
     * 压力节点 名
     *
     * @return -
     */
    public static String getPressureNodeName(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("pressure-node-%s-%s", sceneId, reportId);
        }
        return String.format("pressure-node-%s-%s-%s", sceneId, reportId, tenantId);
    }

}
