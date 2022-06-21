package io.shulie.takin.adapter.cloud.impl.scene.mix;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.scene.mix.SceneMixApi;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneService;
import io.shulie.takin.cloud.biz.service.scene.SceneSynchronizeService;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneDetailV2Response;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.adapter.api.model.response.scenemanage.SynchronizeRequest;
import org.springframework.stereotype.Service;

/**
 * 混合压测场景管理
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class SceneMixApiImpl implements SceneMixApi {

    @Resource
    private CloudSceneService cloudSceneService;

    @Resource
    private SceneSynchronizeService sceneSynchronizeService;

    /**
     * 创建压测场景
     *
     * @param request 入参
     * @return 场景自增主键
     */
    @Override
    public Long create(SceneRequest request) {
        return cloudSceneService.create(request);
    }

    /**
     * 更新压测场景
     *
     * @param request 入参
     * @return 操作结果
     */
    @Override
    public Boolean update(SceneRequest request) {
        return cloudSceneService.update(request);
    }

    /**
     * 获取压测场景
     *
     * @param sceneId 场景主键
     * @return 场景详情
     */
    @Override
    public SceneDetailV2Response detail(SceneManageQueryReq req) {
        return cloudSceneService.detail(req.getSceneId());
    }

    /**
     * 同步场景信息
     *
     * @param request 入参
     * @return 同步事务标识
     */
    @Override
    public String synchronize(SynchronizeRequest request) {
        return sceneSynchronizeService.synchronize(request);
    }
}
