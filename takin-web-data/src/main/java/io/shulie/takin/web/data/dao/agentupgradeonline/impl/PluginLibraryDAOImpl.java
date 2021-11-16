package io.shulie.takin.web.data.dao.agentupgradeonline.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.agentupgradeonline.PluginLibraryDAO;
import io.shulie.takin.web.data.mapper.mysql.PluginLibraryMapper;
import io.shulie.takin.web.data.mapper.mysql.PluginTenantRefMapper;
import io.shulie.takin.web.data.model.mysql.PluginLibraryEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryListQueryParam;
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

    @Resource
    private PluginTenantRefMapper pluginTenantRefMapper;

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

    @Override
    public List<PluginLibraryDetailResult> queryByPluginName(String pluginName) {
        List<PluginLibraryEntity> entityList = pluginLibraryMapper.selectList(
            this.getLambdaQueryWrapper()
                .eq(PluginLibraryEntity::getPluginName, pluginName)
                .orderByDesc(PluginLibraryEntity::getVersionNum)
        );
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }

        return CommonUtil.list2list(entityList, PluginLibraryDetailResult.class);
    }

    @Override
    public List<String> queryAllPluginName() {
        // TODO ocean_wll 如果是超级管理员，则可以查看所有的插件, 现在先设置为null
        PluginLibraryListQueryParam queryParam = new PluginLibraryListQueryParam();
        Page<PluginLibraryEntity> page = new Page<>(0, -1);
        queryParam.setTenantId(null);
        IPage<PluginLibraryEntity> entityList = pluginTenantRefMapper.selectList(page, queryParam);

        return entityList.getRecords().stream().map(PluginLibraryEntity::getPluginName).distinct().collect(
            Collectors.toList());
    }

    @Override
    public PagingList<PluginLibraryDetailResult> page(PluginLibraryListQueryParam query) {

        Page<PluginLibraryEntity> page = new Page<>(query.getRealCurrent(), query.getPageSize());
        IPage<PluginLibraryEntity> entityList = pluginTenantRefMapper.selectList(page, query);

        List<PluginLibraryEntity> records = entityList.getRecords();
        if (records.isEmpty()) {
            return PagingList.empty();
        }
        return PagingList.of(CommonUtil.list2list(records, PluginLibraryDetailResult.class), entityList.getTotal());
    }

    @Override
    public List<PluginLibraryDetailResult> list(List<Long> pluginIds) {
        List<PluginLibraryEntity> entityList = pluginLibraryMapper.selectList(
                this.getLambdaQueryWrapper()
                        .in(PluginLibraryEntity::getId, pluginIds)
        );
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }

        return CommonUtil.list2list(entityList, PluginLibraryDetailResult.class);
    }
}

