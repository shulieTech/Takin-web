package io.shulie.takin.web.data.dao.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.data.param.agentupgradeonline.CreatePluginTenantRefParam;

/**
 * 插件版本库(PluginTenantRef)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-10 17:54:08
 */
public interface PluginTenantRefDAO {

    /**
     * 批量插入
     *
     * @param list 需要插入的数据
     */
    void batchInsert(List<CreatePluginTenantRefParam> list);

}

