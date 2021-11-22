package io.shulie.takin.web.data.dao.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeRefParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeRefDetailResult;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:41:55
 */
public interface ApplicationPluginUpgradeRefDAO {

    //List<ApplicationPluginUpgradeRefDetailResult> getList(Long pluginId);

    List<ApplicationPluginUpgradeRefDetailResult> getList(String upgradeBatch);

    List<ApplicationPluginUpgradeRefDetailResult> getList(List<String> upgradeBatchs);

    /**
     * 批量创建
     *
     * @param upgradeRefs CreateApplicationPluginUpgradeRefParam集合
     */
    void batchCreate(List<CreateApplicationPluginUpgradeRefParam> upgradeRefs);

}

