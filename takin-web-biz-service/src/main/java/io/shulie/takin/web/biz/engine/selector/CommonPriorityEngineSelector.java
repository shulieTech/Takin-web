package io.shulie.takin.web.biz.engine.selector;

import java.util.Comparator;
import java.util.List;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import org.springframework.stereotype.Component;

/**
 * 公网优先集群选择，此处不会强制要求一定要是公网
 */
@Component
public class CommonPriorityEngineSelector implements EngineSelector {

    @Override
    public WatchmanClusterResponse select(List<WatchmanClusterResponse> clusters) {
        clusters.sort(Comparator.comparing(WatchmanClusterResponse::getType));
        return clusters.get(0);
    }

    @Override
    public EngineSelectorStrategy strategy() {
        return EngineSelectorStrategy.COMMON_PRIORITY;
    }
}
