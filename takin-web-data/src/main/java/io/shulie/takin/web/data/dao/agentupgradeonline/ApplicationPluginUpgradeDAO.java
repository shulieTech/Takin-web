package io.shulie.takin.web.data.dao.agentupgradeonline;

import java.util.List;
import java.util.Set;

import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;

/**
 * 应用升级单(ApplicationPluginUpgrade)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
public interface ApplicationPluginUpgradeDAO {

    List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs);

    List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status);

    /**
     * 指定应用id及状态查询最新的升级单
     *
     * @param applicationId 应用Id
     * @param status        升级单状态
     * @return ApplicationPluginUpgradeDetailResult对象
     */
    ApplicationPluginUpgradeDetailResult queryLatestUpgradeByAppIdAndStatus(Long applicationId, Integer status);

}

