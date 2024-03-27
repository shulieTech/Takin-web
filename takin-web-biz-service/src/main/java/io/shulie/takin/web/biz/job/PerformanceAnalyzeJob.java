package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.perfomanceanaly.ThreadAnalyService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2021/6/15 5:50 下午
 */
@Component
@ElasticSchedulerJob(jobName = "performanceAnalyzeJob", cron = "0 0 5 * * ?", description = "性能分析-每天早晨5点执行一次，mysql 清理 5 天, 之前的统计数据")
@Slf4j
public class PerformanceAnalyzeJob implements SimpleJob {

    @Autowired
    private ThreadAnalyService threadAnalyService;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    private String key ="performance_analyze_job_key";
    @Override
    public void execute(ShardingContext shardingContext) {
        Integer second = Integer.valueOf(ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_PERFORMANCE_CLEAR_SECOND));
        if (WebPluginUtils.isOpenVersion()) {
            if (distributedLock.checkLock(key)) {
                return;
            }
            try {
                boolean tryLock = distributedLock.tryLock(key, 0L, 1L, TimeUnit.MINUTES);
                if (!tryLock) {
                    return;
                }
                // 私有化 + 开源
                threadAnalyService.clearData(second);
            } finally {
                distributedLock.unLockSafely(key);
            }
        } else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExts) {
                if (CollectionUtils.isEmpty(ext.getEnvs())) {
                    continue;
                }
                for (TenantEnv e : ext.getEnvs()) {
                    // 分布式锁
                    String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(), e.getEnvCode(), shardingContext.getJobName());
                    if (distributedLock.checkLock(lockKey)) {
                        continue;
                    }
                    jobThreadPool.execute(() -> {
                        boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                        if (!tryLock) {
                            return;
                        }
                        try {
                            WebPluginUtils.setTraceTenantContext(new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(), ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                            threadAnalyService.clearData(second);
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
