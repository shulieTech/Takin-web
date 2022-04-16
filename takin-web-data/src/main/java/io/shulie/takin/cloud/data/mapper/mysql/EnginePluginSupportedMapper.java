package io.shulie.takin.cloud.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.EnginePluginSupportedVersionEntity;

/**
 * 引擎插件支持Mapper
 *
 * @author lipeng
 * @date 2021-01-12 4:43 下午
 */
public interface EnginePluginSupportedMapper extends BaseMapper<EnginePluginSupportedVersionEntity> {

    /**
     * 根据插件id删除支持的版本信息
     *
     * @param pluginId 插件id
     * @return -
     */
    int deleteSupportedVersionsByPluginId(Long pluginId);

}