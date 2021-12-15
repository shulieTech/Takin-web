package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.beans.factory.annotation.Qualifier;

import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author 无涯
 * @date 2021/6/9 3:03 下午
 */

@Component
@ElasticSchedulerJob(jobName = "appRemoteCallJob", cron = "0 0/5 * * * ? *", description = "同步大数据远程调用数据，即入口数据")
@Slf4j
public class AppRemoteCallJob implements SimpleJob {

    @Resource
    private AppRemoteCallService appRemoteCallService;
    @Resource
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;
    @Resource
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {

        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            if (ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REMOTE_CALL_SYNC)) {
                appRemoteCallService.syncAmdb();
            }

        } else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        WebPluginUtils.setTraceTenantContext(
                            new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                        appRemoteCallService.syncAmdb();
                    }
                }
            }
        }
    }

}
