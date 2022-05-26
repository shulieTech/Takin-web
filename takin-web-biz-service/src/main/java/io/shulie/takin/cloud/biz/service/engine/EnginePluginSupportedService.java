package io.shulie.takin.cloud.biz.service.engine;

import java.util.List;

/**
 * 引擎插件支持接口
 *
 * @author lipeng
 * @date 2021-01-12 4:36 下午
 */
public interface EnginePluginSupportedService {

    /**
     * 根据插件id移除支持的版本
     *
     * @param pluginId 插件id
     */
    void removeSupportedVersionsByPluginId(Long pluginId);

    /**
     * 批量保存支持的插件版本
     *
     * @param supportedVersions 支持的插件版本信息
     * @param pluginId          插件id
     */
    void batchSaveSupportedVersions(List<String> supportedVersions, Long pluginId);

    /**
     * 根据插件id获取支持的版本号
     *
     * @param pluginId
     * @return -
     */
    List<String> findSupportedVersionsByPluginId(Long pluginId);
}
