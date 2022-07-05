package io.shulie.takin.web.biz.service.scene.impl;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.scene.SceneMonitorListResponse;
import io.shulie.takin.web.biz.service.scene.SceneMonitorService;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.dao.scene.SceneMonitorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 第三方登录服务表(SceneMonitor)service
 *
 * @author liuchuan
 * @date 2021-12-29 10:26:43
 */
@Service
public class SceneMonitorServiceImpl implements SceneMonitorService {

    @Autowired
    private SceneMonitorDAO sceneMonitorDAO;

    @Override
    public List<SceneMonitorListResponse> listBySceneId(Long sceneId) {
        return DataTransformUtil.list2list(sceneMonitorDAO.listBySceneId(sceneId), SceneMonitorListResponse.class);
    }

}
