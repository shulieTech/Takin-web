package io.shulie.takin.web.biz.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2021/6/9 3:03 下午
 */

@Component
@Slf4j
public class AppRemoteCallJob  {
    @Resource
    private AppRemoteCallService appRemoteCallService;

    @Value("${fix.remote.call.data:false}")
    private Boolean fixData;

    @Resource
    @Qualifier("remoteCallThreadPool")
    private ThreadPoolExecutor remoteCallThreadPool;

    @Resource
    private DistributedLock distributedLock;

    @XxlJob("appRemoteCallJobExecute")
    public void execute() {
        if (fixData) {
            return;
        }
        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            if (ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REMOTE_CALL_SYNC)) {
                appRemoteCallService.syncAmdb();
            }
        } else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExts) {
                if (CollectionUtils.isEmpty(ext.getEnvs())) {
                    continue;
                }
                // 开始数据层分片
                for (TenantEnv e : ext.getEnvs()) {
                    // 分布式锁
                    String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(), e.getEnvCode(), "appRemoteCallJobExecute");
                    if (distributedLock.checkLock(lockKey)) {
                        continue;
                    }
                    remoteCallThreadPool.execute(() -> {
                        boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                        if (!tryLock) {
                            return;
                        }
                        try {
                            WebPluginUtils.setTraceTenantContext(
                                    new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                            ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                            appRemoteCallService.syncAmdb();
                        } finally {
                            distributedLock.unLockSafely(lockKey);
                        }
                    });
//                    // 分片key
//                    int shardKey = (ext.getTenantId() + e.getEnvCode()).hashCode() & Integer.MAX_VALUE;
//                    if (shardKey % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex()) {
//
//                    }

                }
            }
        }
    }

}
