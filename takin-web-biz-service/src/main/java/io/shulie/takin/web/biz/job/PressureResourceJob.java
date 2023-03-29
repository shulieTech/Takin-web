package io.shulie.takin.web.biz.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PressureResourceJob {
    @Resource
    private PressureResourceCommonService pressureResourceCommonService;

    @Resource
    private DistributedLock distributedLock;

    @Resource
    @Qualifier("pressureResourceThreadPool")
    private ThreadPoolExecutor pressureResourceThreadPool;

    @XxlJob("pressureResourceJobExecute")
    public void execute() {
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        for (TenantInfoExt ext : tenantInfoExts) {
            if (CollectionUtils.isEmpty(ext.getEnvs())) {
                continue;
            }
            // 开始数据层分片
            for (TenantInfoExt.TenantEnv e : ext.getEnvs()) {
                // 分片key
                int shardKey = (ext.getTenantId() + e.getEnvCode()).hashCode() & Integer.MAX_VALUE;
                if (shardKey % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex()) {
                    // 分布式锁
                    String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(), e.getEnvCode(), "pressureResourceJobExecute");
                    if (distributedLock.checkLock(lockKey)) {
                        continue;
                    }
                    pressureResourceThreadPool.execute(() -> {
                        try {
                            boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                            if (!tryLock) {
                                return;
                            }
                            WebPluginUtils.setTraceTenantContext(
                                    new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                            ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                            pressureResourceCommonService.processAutoPressureResource();
                        } finally {
                            distributedLock.unLockSafely(lockKey);
                        }
                    });
                }

            }
        }
    }
}
