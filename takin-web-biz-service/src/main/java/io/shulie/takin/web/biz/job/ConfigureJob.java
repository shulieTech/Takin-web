package io.shulie.takin.web.biz.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 6:23 下午
 * todo 不知道做什么用
 */
@Component
@ElasticSchedulerJob(jobName = "configureJob", cron = "0/30 * * * * ?", description = "agent接收的关闭信息后不再上报信息")
@Slf4j
public class ConfigureJob implements SimpleJob {
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {

        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            applicationService.configureTasks();
        } else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            List<CompletableFuture<Void>> futureList = new ArrayList<>(tenantInfoExts.size() << 1);
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
                    Runnable r = () -> {
                        boolean tryLock = distributedLock.tryLock(lockKey, 0L, 1L, TimeUnit.MINUTES);
                        if (!tryLock) {
                            return;
                        }
                        try {
                            WebPluginUtils.setTraceTenantContext(new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                    ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                            applicationService.configureTasks();
                            WebPluginUtils.removeTraceContext();
                        } finally {
                            distributedLock.unLockSafely(lockKey);
                        }
                    };
                    futureList.add(CompletableFuture.runAsync(r, jobThreadPool));
                }
            }
            try {
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).get();
            } catch (Exception ignore) {}
        }
    }
}
