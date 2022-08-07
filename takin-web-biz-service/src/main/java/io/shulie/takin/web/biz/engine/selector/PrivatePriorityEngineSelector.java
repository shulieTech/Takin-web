package io.shulie.takin.web.biz.engine.selector;

import java.util.Comparator;
import java.util.List;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import org.springframework.stereotype.Component;

/**
 * 私网优先集群选择，此处不会强制要求一定要是私网
 */
@Component
public class PrivatePriorityEngineSelector implements EngineSelector {

    @Override
    public WatchmanClusterResponse select(List<WatchmanClusterResponse> clusters) {
        clusters.sort(Comparator.comparing(WatchmanClusterResponse::getType).reversed());
        return clusters.get(0);
    }

    @Override
    public EngineSelectorStrategy strategy() {
        return EngineSelectorStrategy.PRIVATE_PRIORITY;
    }
}
