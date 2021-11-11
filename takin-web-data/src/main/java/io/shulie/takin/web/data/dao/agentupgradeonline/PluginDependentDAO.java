package io.shulie.takin.web.data.dao.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginDependentDetailResult;

/**
 * 插件依赖库(PluginDependent)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:46:16
 */
public interface PluginDependentDAO {

    /**
     * 查询插件依赖库(PluginDependent)表数据
     *
     * @param pluginName    插件名称
     * @param pluginVersion 插件版本
     * @return PluginDependentDetailResult
     */
    List<PluginDependentDetailResult> queryPluginDependentDetailList(String pluginName, String pluginVersion);

}

