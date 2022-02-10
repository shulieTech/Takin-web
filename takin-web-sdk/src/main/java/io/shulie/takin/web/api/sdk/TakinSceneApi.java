package io.shulie.takin.web.api.sdk;

import io.shulie.takin.web.api.client.AbstractTakinWebClient;
import io.shulie.takin.web.api.request.TakinWebSceneRequest;
import io.shulie.takin.web.api.response.ResponseResult;
import io.shulie.takin.web.api.response.SceneDetailResponse;

/**
 * @author caijianying
 */
public interface TakinSceneApi {

    /**
     * 场景列表
     *
     * @param webClient
     * @param request   搜索条件
     * @return
     */
    ResponseResult getSceneListPage(AbstractTakinWebClient webClient, TakinWebSceneRequest request);

    /**
     * 场景详情
     *
     * @param webClient
     * @param sceneId
     * @return
     */
    ResponseResult<SceneDetailResponse> getSceneDetail(AbstractTakinWebClient webClient, Long sceneId);

    /**
     * 发起压测
     *
     * @param webClient
     * @param sceneId
     * @return
     */
    ResponseResult startTask(AbstractTakinWebClient webClient, Long sceneId);

}
