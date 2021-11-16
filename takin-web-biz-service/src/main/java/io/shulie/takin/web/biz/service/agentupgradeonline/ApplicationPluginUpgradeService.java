package io.shulie.takin.web.biz.service.agentupgradeonline;

import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;

import java.util.List;
import java.util.Set;

/**
 * 应用升级单(ApplicationPluginUpgrade)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:41
 */
public interface ApplicationPluginUpgradeService {

    List<ApplicationPluginUpgradeDetailResult> getList(Set<String> upgradeBatchs);

    List<ApplicationPluginUpgradeDetailResult> getListByStatus(Integer status);

}
