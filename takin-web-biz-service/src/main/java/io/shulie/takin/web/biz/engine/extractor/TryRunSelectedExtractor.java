package io.shulie.takin.web.biz.engine.extractor;

import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import org.springframework.stereotype.Component;

@Component
public class TryRunSelectedExtractor implements SelectedExtractor {

    @Resource
    private SceneService sceneService;

    @Override
    public WatchmanClusterResponse extract(ExtractorContext context) {
        SceneResult sceneResult = sceneService.getScene(context.getId());
        if (Objects.isNull(sceneResult)) {
            return DEFAULT_RESPONSE;
        }
        return extraFromFeatures(sceneResult.getFeatures());
    }

    @Override
    public ExtractorType type() {
        return ExtractorType.TRY_RUN;
    }
}
