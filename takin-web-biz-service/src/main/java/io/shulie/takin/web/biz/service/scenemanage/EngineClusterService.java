package io.shulie.takin.web.biz.service.scenemanage;

import java.util.List;

import io.shulie.takin.web.biz.engine.selector.EngineSelectorStrategy;
import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;

public interface EngineClusterService {

    List<WatchmanClusterResponse> clusters();

    WatchmanClusterResponse selectOne();

    WatchmanClusterResponse selectOneOfStrategy(EngineSelectorStrategy strategy);

    WatchmanClusterResponse extractLastExecExtract(Long id, Integer type);
}
