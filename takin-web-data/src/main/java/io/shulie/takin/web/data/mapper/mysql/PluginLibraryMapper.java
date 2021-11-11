package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.PluginLibraryEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryQueryParam;

/**
 * 插件版本库(PluginLibrary)表数据库 mapper
 *
 * @author ocean_wll
 * @date 2021-11-09 20:47:19
 */
public interface PluginLibraryMapper extends BaseMapper<PluginLibraryEntity> {

    /**
     * 查询插件列表
     *
     * @param queryParamList 查询条件
     * @return PluginLibraryEntity 集合
     */
    List<PluginLibraryEntity> queryList(List<PluginLibraryQueryParam> queryParamList);

}

