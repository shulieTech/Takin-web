package io.shulie.takin.web.biz.init.sync;

import java.util.List;

import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
@Slf4j
public class ConfigSynchronizer {

    @Autowired
    private ConfigSyncService configSyncService;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 项目启动后，去更新下配置
     */
    public void initSyncAgentConfig() {
        // 企业版不执行
        if (WebPluginUtils.checkUserPlugin()) {
            return;
        }
        log.info("项目启动，重新同步信息去配置中心");
        List<ApplicationDetailResult> applications = applicationService.getAllApplications();
        if (CollectionUtils.isEmpty(applications)) {
            return;
        } else {
            for (ApplicationDetailResult application : applications) {
                configSyncService.syncGuard(WebPluginUtils.traceTenantCommonExt(), application.getApplicationId(),
                    application.getApplicationName());
                sleep();
                configSyncService.syncShadowDB(WebPluginUtils.traceTenantCommonExt(), application.getApplicationId(),
                    application.getApplicationName());
                sleep();
                configSyncService.syncAllowList(WebPluginUtils.traceTenantCommonExt(), application.getApplicationId(),
                    application.getApplicationName());
                sleep();
                configSyncService.syncShadowJob(WebPluginUtils.traceTenantCommonExt(), application.getApplicationId(),
                    application.getApplicationName());
                sleep();
                configSyncService.syncShadowConsumer(WebPluginUtils.traceTenantCommonExt(), application.getApplicationId(),
                    application.getApplicationName());
            }
        }
        configSyncService.syncClusterTestSwitch(WebPluginUtils.traceTenantCommonExt());
        sleep();
        configSyncService.syncAllowListSwitch(WebPluginUtils.traceTenantCommonExt());
        sleep();
        configSyncService.syncBlockList(WebPluginUtils.traceTenantCommonExt());
        log.info("所有配置同步到配置中心成功");
    }

    /**
     * 给zk写的压力不要太大，慢慢写
     */
    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            //ignore
        }
    }
}
