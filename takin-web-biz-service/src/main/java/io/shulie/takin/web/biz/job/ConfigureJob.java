package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.ApplicationService;
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

    @Override
    public void execute(ShardingContext shardingContext) {

        if(WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            applicationService.configureTasks();
        }else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        jobThreadPool.execute(() -> {
                            WebPluginUtils.setTraceTenantContext(new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                            applicationService.configureTasks();
                            WebPluginUtils.removeTraceContext();
                        });
                    }
                }
            }
        }
    }
}
