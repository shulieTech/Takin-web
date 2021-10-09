package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:36 下午
 */
@Component
@ElasticSchedulerJob(jobName = "sceneSchedulerJob", cron = "0 */1 * * * ?", description = "压测场景定时执行，一分钟检查一次，待压测场景执行")
@Slf4j
public class SceneSchedulerJob implements SimpleJob {
    @Autowired
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if(CollectionUtils.isEmpty(tenantInfoExts)) {
            // 私有化 + 开源
            sceneSchedulerTaskService.executeSchedulerPressureTask(null);
        }else {
            // saas
            tenantInfoExts.forEach(t -> {
                // 根据环境 分线程
                t.getEnvs().forEach(e ->
                    jobThreadPool.execute(() ->  sceneSchedulerTaskService.executeSchedulerPressureTask(new TenantCommonExt(t.getTenantId(),t.getUserAppKey(),e.getEnvCode()))));
            });
        }

    }
}
