package io.shulie.takin.web.biz.service.scene;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.scene.SceneMonitorListResponse;

/**
 * 第三方登录服务表(SceneMonitor)service
 *
 * @author liuchuan
 * @date 2021-12-29 10:26:43
 */
public interface SceneMonitorService {

    /**
     * 通过场景id获得监控列表
     *
     * @param sceneId 场景id
     * @return 监控列表
     */
    List<SceneMonitorListResponse> listBySceneId(Long sceneId);

}
