package io.shulie.takin.web.biz.utils.job;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.utils.job
 * @ClassName: JobRedisUtils
 * @Description: TODO
 * @Date: 2021/11/30 12:09
 */
public class JobRedisUtils {

    private static String REDIS_JOB = "redis:job:%s:%s:%s";
    /**
     * 报告redis
     */
    private static String REDIS_JOB_REPORT = "redis:job:report:%s:%s:%s";
    /**
     * 定时压测
     */
    private static String REDIS_SCHEDULER_JOB = "redis:scheduler:job:%s:%s:%s";

    /**
     * 获取job分布式锁
     * @param tenantId
     * @param envCode
     * @param jobName
     * @return
     */
    public static String getJobRedis(Long tenantId,String envCode,String jobName) {
        return String.format(REDIS_JOB,tenantId,envCode,jobName);
    }

    /**
     * 获取job分布式锁
     * @param tenantId
     * @param envCode
     * @param reportId
     * @return
     */
    public static String getRedisJobReport(Long tenantId,String envCode,Long reportId) {
        return String.format(REDIS_JOB_REPORT,tenantId,envCode,reportId);
    }

    public static String getRedisJobResource(Long tenantId,String envCode,Long reportId) {
        return String.format(REDIS_JOB_REPORT,tenantId,envCode,reportId);
    }

    /**
     * 获取job分布式锁
     * @param tenantId
     * @param envCode
     * @param id
     * @return
     */
    public static String getSchedulerRedis(Long tenantId,String envCode,Long id) {
        return String.format(REDIS_SCHEDULER_JOB,tenantId,envCode,id);
    }
}
