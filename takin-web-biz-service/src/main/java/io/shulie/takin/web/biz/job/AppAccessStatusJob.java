package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "appAccessStatusJob",
    // 时效转移
    misfire = true,
    // 重新执行
    failover = true,
    cron = "0/10 * *  * * ?", description = "同步大数据应用状态")
@Slf4j
public class AppAccessStatusJob implements SimpleJob {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if(CollectionUtils.isEmpty(tenantInfoExts)) {
            // 私有化 + 开源
            applicationService.syncApplicationAccessStatus();
        }else {
            // saas
            tenantInfoExts.forEach(t -> {
                // 根据环境 分线程
                t.getEnvs().forEach(e ->
                    jobThreadPool.execute(() ->applicationService.syncApplicationAccessStatus(t.getTenantId(),t.getKey(),e.getEnvCode())));
            });

        }

    }
}
