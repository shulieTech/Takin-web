package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.beans.factory.annotation.Qualifier;

import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;

/**
 * @author 无涯
 * @date 2021/6/15 5:36 下午
 */
@Component
@ElasticSchedulerJob(jobName = "sceneSchedulerJob", cron = "0 */1 * * * ?", description = "压测场景定时执行，一分钟检查一次，待压测场景执行")
@Slf4j
public class SceneSchedulerJob implements SimpleJob {
    @Resource
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @Resource
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {

        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            sceneSchedulerTaskService.executeSchedulerPressureTask();
        } else {
            List<TenantInfoExt> tenantInfoExtList = WebPluginUtils.getTenantInfoList();
            // saas
            tenantInfoExtList.forEach(ext -> {
                // 根据环境 分线程
                ext.getEnvs().forEach(e ->
                    jobThreadPool.execute(() -> {
                        WebPluginUtils.setTraceTenantContext(new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                            ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                        sceneSchedulerTaskService.executeSchedulerPressureTask();
                        WebPluginUtils.removeTraceContext();
                    }));
            });
        }
    }
}
