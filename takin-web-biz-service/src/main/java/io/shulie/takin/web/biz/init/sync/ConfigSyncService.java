package io.shulie.takin.web.biz.init.sync;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
public interface ConfigSyncService {

    void syncGuard(TenantCommonExt commonExt, long applicationId, String applicationName);

    void syncClusterTestSwitch(TenantCommonExt commonExt);

    void syncAllowListSwitch(TenantCommonExt commonExt);

    void syncAllowList(TenantCommonExt commonExt, long applicationId, String applicationName);

    void syncShadowJob(TenantCommonExt commonExt, long applicationId, String applicationName);

    void syncShadowDB(TenantCommonExt commonExt, long applicationId, String applicationName);

    void syncShadowConsumer(TenantCommonExt commonExt, long applicationId, String applicationName);

    void syncBlockList(TenantCommonExt commonExt);

}
