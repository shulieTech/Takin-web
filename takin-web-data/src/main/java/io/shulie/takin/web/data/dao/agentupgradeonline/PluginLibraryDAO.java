package io.shulie.takin.web.data.dao.agentupgradeonline;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryListQueryParam;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryQueryParam;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;

/**
 * 插件版本库(PluginLibrary)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:47:19
 */
public interface PluginLibraryDAO {

    /**
     * 查询插件列表
     *
     * @param queryParam 查询条件
     * @return PluginLibraryDetailResult集合
     */
    List<PluginLibraryDetailResult> queryList(List<PluginLibraryQueryParam> queryParam);

    /**
     * 根据插件Id查询详情
     *
     * @param pluginId 插件Id
     * @return PluginLibraryDetailResult
     */
    PluginLibraryDetailResult queryById(Long pluginId);

    /**
     * 根据插件名查询集合
     *
     * @param pluginName 插件名
     * @return PluginLibraryDetailResult集合
     */
    List<PluginLibraryDetailResult> queryByPluginName(String pluginName);

    /**
     * 查询当前用户所有可见的插件名
     *
     * @return 插件名集合
     */
    List<String> queryAllPluginName();

    /**
     * 分页查询
     *
     * @param query 分页查询条件
     * @return PluginLibraryDetailResult集合
     */
    PagingList<PluginLibraryDetailResult> page(PluginLibraryListQueryParam query);

    List<PluginLibraryDetailResult> list(List<Long> pluginIds);

}

