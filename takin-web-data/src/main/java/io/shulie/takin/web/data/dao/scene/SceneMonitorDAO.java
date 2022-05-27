package io.shulie.takin.web.data.dao.scene;

import java.util.List;

import io.shulie.takin.web.data.result.SceneMonitorListResult;

/**
 * 第三方登录服务表(SceneMonitor)表数据库 dao 层
 *
 * @author liuchuan
 * @date 2021-12-29 10:20:12
 */
public interface SceneMonitorDAO {

    /**
     * 通过场景id获得监控列表
     *
     * @param sceneId 场景id
     * @return 监控列表
     */
    List<SceneMonitorListResult> listBySceneId(Long sceneId);

}

