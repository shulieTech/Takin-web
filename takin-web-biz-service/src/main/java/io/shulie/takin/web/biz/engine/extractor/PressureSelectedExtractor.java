package io.shulie.takin.web.biz.engine.extractor;

import java.util.Objects;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.scene.manage.CloudSceneManageApi;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import org.springframework.stereotype.Component;

@Component
public class PressureSelectedExtractor implements SelectedExtractor {

    @Resource
    private CloudSceneManageApi cloudSceneManageApi;

    @Override
    public WatchmanClusterResponse extract(ExtractorContext context) {

        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(context.getId());
        SceneManageWrapperResp sceneDetail = cloudSceneManageApi.getSceneDetail(req);
        if (Objects.isNull(sceneDetail)) {
            return DEFAULT_RESPONSE;
        }
        return extraFromFeatures(sceneDetail.getFeatures());
    }

    @Override
    public ExtractorType type() {
        return ExtractorType.PRESSURE;
    }
}
