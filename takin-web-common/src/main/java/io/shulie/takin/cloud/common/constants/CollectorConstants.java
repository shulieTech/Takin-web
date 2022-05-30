package io.shulie.takin.cloud.common.constants;

/**
 * 常量
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 15:55
 */
public class CollectorConstants {

    /**
     * redis key前缀
     */
    public static final String REDIS_PRESSURE_TASK_KEY = "COLLECTOR:JOB:TASK:%s";

    /**
     * 窗口大小
     */
    public static int[] timeWindow = new int[] {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
    /**
     * 单位：秒
     */
    public static final int OVERDUE_SECOND = 10;
    /**
     * 10秒过期策略，超时丢弃Metrics 数据，单位：毫秒
     */
    public static final long OVERDUE_TIME = 2000 * OVERDUE_SECOND;
    /**
     * Metrics 统计时间间隔
     */
    public static final int SEND_TIME = 5;
    public static final int SECOND_60 = 60;

}
