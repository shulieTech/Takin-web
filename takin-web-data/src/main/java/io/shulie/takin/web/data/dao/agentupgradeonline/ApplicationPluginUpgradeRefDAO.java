package io.shulie.takin.web.data.dao.agentupgradeonline;

import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeRefParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;

import java.util.List;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:41:55
 */
public interface ApplicationPluginUpgradeRefDAO {

    List<ApplicationPluginUpgradeRefDetailResult> getList(String pluginName, String pluginVersion);

    List<ApplicationPluginUpgradeRefDetailResult> getList(String upgradeBatch);

    List<ApplicationPluginUpgradeRefDetailResult> getList(List<String> upgradeBatchs);

    /**
     * 批量创建
     *
     * @param upgradeRefs CreateApplicationPluginUpgradeRefParam集合
     */
    void batchCreate(List<CreateApplicationPluginUpgradeRefParam> upgradeRefs);

}

