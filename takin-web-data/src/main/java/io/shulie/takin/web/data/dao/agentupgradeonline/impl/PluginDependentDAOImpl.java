package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginDependentDAO;
import io.shulie.takin.web.data.mapper.mysql.PluginDependentMapper;
import io.shulie.takin.web.data.model.mysql.PluginDependentEntity;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginDependentDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 插件依赖库(PluginDependent)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:46:16
 */
@Service
public class PluginDependentDAOImpl implements PluginDependentDAO, MPUtil<PluginDependentEntity> {

    @Resource
    private PluginDependentMapper pluginDependentMapper;

    @Override
    public List<PluginDependentDetailResult> queryPluginDependentDetailList(String pluginName, String pluginVersion) {
        List<PluginDependentEntity> entityList = pluginDependentMapper.selectList(this.getLambdaQueryWrapper()
            .eq(PluginDependentEntity::getPluginName, pluginName)
            .eq(PluginDependentEntity::getPluginVersion, pluginVersion));

        return CommonUtil.list2list(entityList, PluginDependentDetailResult.class);
    }
}

