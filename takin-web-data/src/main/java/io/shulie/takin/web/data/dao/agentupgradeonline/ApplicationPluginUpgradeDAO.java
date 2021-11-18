package io.shulie.takin.web.data.dao.agentupgradeonline;

import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;

import java.util.List;
import java.util.Set;

/**
 * 应用升级单(ApplicationPluginUpgrade)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
public interface ApplicationPluginUpgradeDAO {

    List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs);

    List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status);

}

