package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginLibraryDAO;
import io.shulie.takin.web.data.mapper.mysql.PluginLibraryMapper;
import io.shulie.takin.web.data.model.mysql.PluginLibraryEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryQueryParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 插件版本库(PluginLibrary)表数据库 dao 层实现
 *
 * @author ocean_wll
 * @date 2021-11-09 20:47:19
 */
@Service
public class PluginLibraryDAOImpl implements PluginLibraryDAO, MPUtil<PluginLibraryEntity> {

    @Resource
    private PluginLibraryMapper pluginLibraryMapper;

    @Override
    public List<PluginLibraryDetailResult> queryList(List<PluginLibraryQueryParam> queryParam) {
        if (CollectionUtils.isEmpty(queryParam)) {
            return new ArrayList<>();
        }
        List<PluginLibraryEntity> pluginLibraryEntityList = pluginLibraryMapper.queryList(queryParam);
        return CommonUtil.list2list(pluginLibraryEntityList, PluginLibraryDetailResult.class);
    }

    @Override
    public PluginLibraryDetailResult queryById(Long pluginId) {
        PluginLibraryEntity entity = pluginLibraryMapper.selectById(pluginId);
        if (entity == null) {
            return null;
        }
        PluginLibraryDetailResult detailResult = new PluginLibraryDetailResult();
        BeanUtils.copyProperties(entity, detailResult);
        return detailResult;
    }
}

