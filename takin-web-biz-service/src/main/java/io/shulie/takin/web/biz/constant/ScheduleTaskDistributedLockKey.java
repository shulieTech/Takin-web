package io.shulie.takin.web.biz.constant;

/**
 * @author mubai
 * @date 2020-11-25 10:03
 */
public class ScheduleTaskDistributedLockKey {
    //压力机趋势统计定时任务，分布式锁
    public static final String PRESSURE_MACHINE_STATISTICS_SCHEDULE_TASK_KEY = "pressure:machine:statistics:schedule:task:key";
    //压力机离线状态定时判断，分布式锁
    public static final String PRESSURE_MACHINE_OFFLINE_STATUS_SCHEDULE_CALCULATE_KEY = "pressure:mcahine:offline:status:calculate:key";

    public static final String SCENE_SCHEDULER_PRESSURE_TASK_KEY = "scene:scheduler:pressure:task:key";

}
