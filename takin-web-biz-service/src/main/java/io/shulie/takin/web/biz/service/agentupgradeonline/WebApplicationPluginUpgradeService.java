package io.shulie.takin.web.biz.service.agentupgradeonline;

import io.shulie.takin.web.data.result.agentUpgradeOnline.ApplicationPluginUpgradeDetailResult;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:41
 */
public interface WebApplicationPluginUpgradeService {


    /**
     * 根据ApplicationId和状态查询最新升级单
     *
     * @param applicationId 应用Id
     * @param status        状态
     * @return ApplicationPluginUpgradeDetailResult对象
     */
    ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status);
}
