package io.shulie.takin.web.biz.engine.extractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import static io.shulie.takin.web.biz.engine.extractor.SelectedExtractor.DEFAULT_RESPONSE;

@Component
public class CompositeSelectedExtractor implements InitializingBean {

    @Resource
    private List<SelectedExtractor> extractors = Collections.emptyList();

    private Map<ExtractorType, SelectedExtractor> extractorMap;

    public WatchmanClusterResponse extract(ExtractorContext context) {
        ExtractorType extractorType;
        if (context == null
            || Objects.isNull(context.getId()) || Objects.isNull(extractorType = context.getExtractorType())) {
            return DEFAULT_RESPONSE;
        }
        return extractorMap.get(extractorType).extract(context);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        extractorMap = extractors.stream().collect(Collectors.toMap(SelectedExtractor::type, Function.identity()));
    }
}
