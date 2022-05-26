package io.shulie.takin.adapter.api.entrypoint.scene.mix;

import io.shulie.takin.adapter.api.model.request.scenemanage.SceneManageQueryReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneDetailV2Response;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.adapter.api.model.response.scenemanage.SynchronizeRequest;

/**
 * 混合压测场景SDK接口
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@SuppressWarnings("unused")
public interface SceneMixApi {
    /**
     * 创建压测场景
     *
     * @param request 入参
     * @return 场景自增主键
     */
    Long create(SceneRequest request);

    /**
     * 更新压测场景
     *
     * @param request 入参
     * @return 操作结果
     */
    Boolean update(SceneRequest request);

    /**
     * 获取压测场景
     *
     * @param sceneId 入参-只需要给场景主键赋值即可
     * @return 场景详情
     */
    SceneDetailV2Response detail(SceneManageQueryReq sceneId);

    /**
     * 同步场景信息
     *
     * @param request 入参
     * @return 同步事务标识
     */
    String synchronize(SynchronizeRequest request);
}
