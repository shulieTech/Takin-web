package io.shulie.takin.web.data.dao.agentupgradeonline;

import io.shulie.takin.web.data.result.agentUpgradeOnline.ApplicationPluginUpgradeDetailResult;

/**
 * 应用升级单(ApplicationPluginUpgrade)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
public interface WebApplicationPluginUpgradeDAO {

    /**
     * 指定应用id及状态查询最新的升级单
     *
     * @param applicationId 应用Id
     * @param status        升级单状态
     * @return ApplicationPluginUpgradeDetailResult对象
     */
    ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status);
}

