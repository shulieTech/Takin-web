package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
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
 * @date 2021/6/15 5:40 下午
 */
@Component
@ElasticSchedulerJob(jobName = "showdownVerifyJob", cron = "0/10 * *  * * ?", description = "漏数验证")
@Slf4j
public class ShowdownVerifyJob implements SimpleJob {
    @Autowired
    private VerifyTaskService verifyTaskService;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;
    @Override
    public void execute(ShardingContext shardingContext) {
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if(CollectionUtils.isEmpty(tenantInfoExts)) {
            // 私有化 + 开源
            verifyTaskService.showdownVerifyTask();
        }else {
            // saas
            tenantInfoExts.forEach(ext -> {
                // 根据环境 分线程
                ext.getEnvs().forEach(e ->
                    jobThreadPool.execute(() ->  {
                        WebPluginUtils.setTraceTenantContext(new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                            ext.getTenantCode(),ContextSourceEnum.JOB.getCode()));
                        verifyTaskService.showdownVerifyTask();
                        WebPluginUtils.removeTraceContext();
                    }));
            });
        }
    }
}
