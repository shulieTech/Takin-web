package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
import io.shulie.takin.web.biz.service.agentupgradeonline.PluginDependentService;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginDependentDAO;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginDependentDetailResult;
import org.springframework.stereotype.Service;

/**
 * 插件依赖库(PluginDependent)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:27:46
 */
@Service
public class PluginDependentServiceImpl implements PluginDependentService {

    @Resource
    private PluginDependentDAO pluginDependentDAO;

    @Override
    public List<PluginInfo> queryDependent(String pluginName, String pluginVersion) {
        List<PluginDependentDetailResult> list = pluginDependentDAO.queryPluginDependentDetailList(pluginName,
            pluginVersion);
        return list.stream().map(item -> {
            PluginInfo pluginInfo = new PluginInfo();
            pluginInfo.setPluginName(item.getDependentPluginName());
            pluginInfo.setVersion(item.getDependentPluginVersion());
            return pluginInfo;
        }).collect(Collectors.toList());
    }
}
