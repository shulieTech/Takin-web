package io.shulie.takin.cloud.biz.service.engine;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.biz.input.engine.EnginePluginWrapperInput;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginDetailOutput;
import io.shulie.takin.cloud.data.model.mysql.EnginePluginEntity;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * 引擎接口
 *
 * @author lipeng
 * @date 2021-01-06 3:07 下午
 */
public interface EnginePluginService {

    /**
     * 查询引擎支持的插件信息
     *
     * @param pluginTypes 插件类型
     * @return -
     */
    Map<String, List<EnginePluginEntity>> findEngineAvailablePluginsByType(List<String> pluginTypes);

    /**
     * 根据插件ID获取插件详情信息
     *
     * @param pluginId 插件主键
     * @return -
     */
    ResponseResult<EnginePluginDetailOutput> findEnginePluginDetails(Long pluginId);

    /**
     * 保存引擎插件
     *
     * @param input 入参
     */
    void saveEnginePlugin(EnginePluginWrapperInput input);

    /**
     * 更改压测引擎状态
     *
     * @param pluginId 插件主键
     * @param status   状态
     */
    void changeEnginePluginStatus(Long pluginId, Boolean status);

}
