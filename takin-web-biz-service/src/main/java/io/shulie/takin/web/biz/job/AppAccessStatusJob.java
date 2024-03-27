package io.shulie.takin.web.biz.job;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2021/6/15 5:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "appAccessStatusJob", cron = "0/10 * *  * * ?", description = "同步大数据应用状态",
        // 时效转移
        misfire = true,
        // 重新执行
        failover = true,
        isSharding = true)
public class AppAccessStatusJob implements SimpleJob {

    @Autowired
    private ApplicationService applicationService;
    @Resource
    @Qualifier("syncAppStatusThreadPool")
    private ThreadPoolExecutor syncAppStatusThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    private String key = "app_access_status_job_key";

    @Override
    public void execute(ShardingContext shardingContext) {
        if (WebPluginUtils.isOpenVersion()) {
            if (distributedLock.checkLock(key)) {
                return;
            }
            try {
                boolean tryLock = distributedLock.tryLock(key, 0L, 9L, TimeUnit.SECONDS);
                if (tryLock) {
                    // 私有化 + 开源
                    applicationService.syncApplicationAccessStatus();
                    return;
                }
            } finally {
                distributedLock.unLockSafely(key);
            }
        }
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        for (TenantInfoExt ext : tenantInfoExts) {
            if (CollectionUtils.isEmpty(ext.getEnvs())) {
                continue;
            }
            // 根据环境 分线程
            for (TenantEnv e : ext.getEnvs()) {
                int shardKey = (ext.getTenantId() + e.getEnvCode()).hashCode() & Integer.MAX_VALUE;
                if (shardKey % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(), e.getEnvCode(), shardingContext.getJobName());
                    if (distributedLock.checkLock(lockKey)) {
                        continue;
                    }
                    syncAppStatusThreadPool.execute(() -> {
                        boolean tryLock = distributedLock.tryLock(lockKey, 0L, 1L, TimeUnit.MINUTES);
                        if (!tryLock) {
                            return;
                        }
                        try {
                            WebPluginUtils.setTraceTenantContext(
                                    new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                            ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                            applicationService.syncApplicationAccessStatus();
                            WebPluginUtils.removeTraceContext();
                        } finally {
                            distributedLock.unLockSafely(lockKey);
                        }
                    });
                }
            }
        }
    }
}
