package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;

/**
 * 插件依赖库(PluginDependent)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:27:45
 */
public interface PluginDependentService {

    /**
     * 查询插件依赖项
     *
     * @param pluginName    插件名
     * @param pluginVersion 插件版本
     * @return PluginInfo集合
     */
    List<PluginInfo> queryDependent(String pluginName, String pluginVersion);

}
