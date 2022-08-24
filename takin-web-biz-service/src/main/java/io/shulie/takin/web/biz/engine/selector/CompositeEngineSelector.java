package io.shulie.takin.web.biz.engine.selector;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static io.shulie.takin.web.biz.engine.selector.EngineSelectorStrategy.PRIVATE_PRIORITY;
import static io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum.TAKIN_ENGINE_SELECTOR_STRATEGY;

@Component
public class CompositeEngineSelector implements InitializingBean {

    @Resource
    private List<EngineSelector> selectors = Collections.emptyList();

    private Map<EngineSelectorStrategy, EngineSelector> selectorMap;

    public WatchmanClusterResponse select(List<WatchmanClusterResponse> clusters, EngineSelectorStrategy strategy) {
        clusters.removeIf(WatchmanClusterResponse::isDisable);
        if (CollectionUtils.isEmpty(clusters)) {
            return null;
        }
        if (Objects.isNull(strategy)) {
            String strategyType = ConfigServerHelper.getValueByKey(TAKIN_ENGINE_SELECTOR_STRATEGY, PRIVATE_PRIORITY.getType());
            strategy = EngineSelectorStrategy.of(strategyType);
        }
        return selectorMap.get(strategy).select(clusters);
    }

    @Override
    public void afterPropertiesSet() {
        selectorMap = selectors.stream().collect(Collectors.toMap(EngineSelector::strategy, Function.identity()));
    }
}
