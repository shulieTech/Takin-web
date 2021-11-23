package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeRefParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;

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

    /**
     * 批量插入
     *
     * @param upgradeRefs CreateApplicationPluginUpgradeRefParam集合
     */
    void batchCreate(List<CreateApplicationPluginUpgradeRefParam> upgradeRefs);

}
