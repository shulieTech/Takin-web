package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.web.data.model.mysql.PluginLibraryEntity;
import io.shulie.takin.web.data.model.mysql.PluginTenantRefEntity;
import io.shulie.takin.web.data.param.agentupgradeonline.PluginLibraryListQueryParam;
import org.apache.ibatis.annotations.Param;

/**
 * 插件版本库(PluginTenantRef)表数据库 mapper
 *
 * @author ocean_wll
 * @date 2021-11-10 17:54:08
 */
public interface PluginTenantRefMapper extends BaseMapper<PluginTenantRefEntity> {

    /**
     * 根据租户id及插件名字搜索插件列表
     *
     * @param queryParam 查询参数
     * @return PluginLibraryEntity集合
     */
    IPage<PluginLibraryEntity> selectList(@Param("page") IPage<PluginLibraryEntity> iPage,
        @Param("param") PluginLibraryListQueryParam queryParam);
}

