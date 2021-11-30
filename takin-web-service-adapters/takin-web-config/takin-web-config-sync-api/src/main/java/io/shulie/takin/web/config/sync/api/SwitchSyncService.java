package io.shulie.takin.web.config.sync.api;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

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
    void turnClusterTestSwitch(TenantCommonExt commonExt, boolean on);

    /**
     * 调整白名单开关
     */
    void turnAllowListSwitch(TenantCommonExt commonExt, boolean on);

}
