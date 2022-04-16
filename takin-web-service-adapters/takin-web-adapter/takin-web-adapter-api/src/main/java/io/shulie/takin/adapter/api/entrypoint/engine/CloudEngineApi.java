package io.shulie.takin.adapter.api.entrypoint.engine;

import java.util.List;
import java.util.Map;

import io.shulie.takin.adapter.api.model.request.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginSimpleInfoResp;

/**
 * 压测引擎Api
 *
 * @author lipeng
 * @date 2021-01-18 5:13 下午
 */
public interface CloudEngineApi {

    /**
     * 根据插件类型获取插件列表
     *
     * @param request -
     * @return -
     */
    Map<String, List<EnginePluginSimpleInfoResp>> listEnginePlugins(EnginePluginFetchWrapperReq request);

    /**
     * 根据插件ID获取插件详情
     *
     * @param request -
     * @return -
     */
    EnginePluginDetailResp getEnginePluginDetails(EnginePluginDetailsWrapperReq request);
}