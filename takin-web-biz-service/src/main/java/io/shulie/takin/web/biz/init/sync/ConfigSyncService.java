package io.shulie.takin.web.biz.init.sync;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
public interface ConfigSyncService {

    void syncGuard(String userAppKey, long applicationId, String applicationName);

    void syncClusterTestSwitch(String userAppKey);

    void syncAllowListSwitch(String userAppKey);

    void syncAllowList(String userAppKey, long applicationId, String applicationName);

    void syncShadowJob(String userAppKey, long applicationId, String applicationName);

    void syncShadowDB(String userAppKey, long applicationId, String applicationName);

    void syncShadowConsumer(String userAppKey, long applicationId, String applicationName);

    void syncBlockList(String userAppKey);

}
