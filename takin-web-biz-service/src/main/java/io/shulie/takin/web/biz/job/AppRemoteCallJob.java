package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/9 3:03 下午
 */

@Component
@ElasticSchedulerJob(jobName = "appRemoteCallJob", cron = "0 0/5 * * * ? *", description = "同步大数据远程调用数据，即入口数据")
@Slf4j
public class AppRemoteCallJob implements SimpleJob {

    @Value("${tro-web.remote-call.sync: true}")
    private boolean remoteCallSync;

    @Autowired
    private AppRemoteCallService appRemoteCallService;


    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {
        if(!remoteCallSync) {
            return;
        }
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if(CollectionUtils.isEmpty(tenantInfoExts)) {
            // 私有化 + 开源
            appRemoteCallService.syncAmdb();
        }else {
            // saas
            tenantInfoExts.forEach(ext -> {
                // 根据环境 分线程
                ext.getEnvs().forEach(e -> {
                    WebPluginUtils.setTraceTenantContext(new TenantCommonExt(ext.getTenantId(),ext.getTenantAppKey(),e.getEnvCode()));
                    jobThreadPool.execute(() -> appRemoteCallService.syncAmdb());
                    WebPluginUtils.removeTraceContext();
                });
            });

        }
    }

}
