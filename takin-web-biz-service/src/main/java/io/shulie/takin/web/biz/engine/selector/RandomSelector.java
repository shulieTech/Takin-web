package io.shulie.takin.web.biz.engine.selector;

import java.util.List;
import java.util.Random;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import org.springframework.stereotype.Component;

@Component
public class RandomSelector implements EngineSelector {

    @Override
    public WatchmanClusterResponse select(List<WatchmanClusterResponse> clusters) {
        return clusters.get(new Random(clusters.size()).nextInt());
    }

    @Override
    public EngineSelectorStrategy strategy() {
        return EngineSelectorStrategy.RANDOM;
    }
}
