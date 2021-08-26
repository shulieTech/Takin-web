package io.shulie.takin.web.config.sync.api;

/**
 * 全局开关类
 *
 * @author shiyajian
 * create: 2020-09-01
 */
public interface SwitchSyncService {

    /**
     * 调整全局压测开关
     */
    void turnClusterTestSwitch(String namespace, boolean on);

    /**
     * 调整白名单开关
     */
    void turnAllowListSwitch(String namespace, boolean on);

}
