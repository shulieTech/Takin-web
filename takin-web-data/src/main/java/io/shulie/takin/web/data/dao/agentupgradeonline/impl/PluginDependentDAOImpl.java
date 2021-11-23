package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginDependentDAO;
import io.shulie.takin.web.data.mapper.mysql.PluginDependentMapper;
import io.shulie.takin.web.data.model.mysql.PluginDependentEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.CreatePluginDependentParam;
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
public class PluginDependentDAOImpl extends ServiceImpl<PluginDependentMapper, PluginDependentEntity>
        implements PluginDependentDAO, MPUtil<PluginDependentEntity> {

    @Resource
    private PluginDependentMapper pluginDependentMapper;

    @Override
    public List<PluginDependentDetailResult> queryPluginDependentDetailList(String pluginName, String pluginVersion) {
        List<PluginDependentEntity> entityList = pluginDependentMapper.selectList(this.getLambdaQueryWrapper()
                .eq(PluginDependentEntity::getPluginName, pluginName)
                .eq(PluginDependentEntity::getPluginVersion, pluginVersion)
                .eq(PluginDependentEntity::getIsDeleted, 0));

        return CommonUtil.list2list(entityList, PluginDependentDetailResult.class);
    }

    @Override
    public void batchInsert(List<CreatePluginDependentParam> list) {
        List<PluginDependentEntity> collect = list.stream()
                .map(item -> Convert.convert(PluginDependentEntity.class, item))
                .collect(Collectors.toList());
        this.saveBatch(collect);
    }
}

