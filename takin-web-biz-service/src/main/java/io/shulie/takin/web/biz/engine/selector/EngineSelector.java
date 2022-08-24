package io.shulie.takin.web.biz.engine.selector;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;

/**
 * 非界面选择时，集群选择器
 */
public interface EngineSelector {

    EngineSelectorStrategy strategy();

    WatchmanClusterResponse select(@NotEmpty List<WatchmanClusterResponse> clusters);
}
