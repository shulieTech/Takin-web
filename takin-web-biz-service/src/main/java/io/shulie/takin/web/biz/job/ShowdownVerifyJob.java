package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:40 下午
 */
@Component
@ElasticSchedulerJob(jobName = "showdownVerifyJob", cron = "0/10 * *  * * ?", description = "漏数验证")
@Slf4j
public class ShowdownVerifyJob implements SimpleJob {

    @Autowired
    private VerifyTaskService verifyTaskService;

    @Autowired
    @Qualifier("showdownVerifyThreadPool")
    private ThreadPoolExecutor showdownVerifyThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {

        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            verifyTaskService.showdownVerifyTask();
        } else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        // 分布式锁
                        String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(),e.getEnvCode(),shardingContext.getJobName());
                        if (distributedLock.checkLock(lockKey)) {
                            continue;
                        }
                        showdownVerifyThreadPool.execute(() -> {
                            boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                            if(!tryLock) {
                                return;
                            }
                            try {
                                WebPluginUtils.setTraceTenantContext(
                                    new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                        ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                                verifyTaskService.showdownVerifyTask();
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
}
