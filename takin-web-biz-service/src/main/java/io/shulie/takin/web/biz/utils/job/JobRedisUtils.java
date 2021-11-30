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
     * 获取job分布式锁
     * @param tenantId
     * @param envCode
     * @param jobName
     * @return
     */
    public static String getJobRedis(Long tenantId,String envCode,String jobName) {
        return String.format(REDIS_JOB,tenantId,envCode,jobName);
    }
}
