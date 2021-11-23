package io.shulie.takin.web.biz.service.agentupgradeonline;

import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;

import java.util.List;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:31:15
 */
public interface ApplicationPluginUpgradeRefService {

    List<ApplicationPluginUpgradeRefDetailResult> getList(String pluginName,String pluginVersion);

    List<ApplicationPluginUpgradeRefDetailResult> getList(String upgradeBatch);

    List<ApplicationPluginUpgradeRefDetailResult> getList(List<String> upgradeBatchs);

}
