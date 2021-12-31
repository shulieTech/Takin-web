package io.shulie.takin.web.data.dao.scene.impl;

import java.util.List;

import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.dao.scene.SceneMonitorDAO;
import io.shulie.takin.web.data.mapper.mysql.SceneMonitorMapper;
import io.shulie.takin.web.data.model.mysql.SceneMonitorEntity;
import io.shulie.takin.web.data.result.SceneMonitorListResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 第三方登录服务表(SceneMonitor)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-12-29 10:20:13
 */
@Service
public class SceneMonitorDAOImpl implements SceneMonitorDAO, MPUtil<SceneMonitorEntity> {

    @Autowired
    private SceneMonitorMapper sceneMonitorMapper;

    @Override
    public List<SceneMonitorListResult> listBySceneId(Long sceneId) {
        return DataTransformUtil.list2list(sceneMonitorMapper.selectList(this.getLambdaQueryWrapper()
            .select(SceneMonitorEntity::getTitle, SceneMonitorEntity::getUrl)
            .eq(SceneMonitorEntity::getSceneId, sceneId).last("limit 1000")), SceneMonitorListResult.class);
    }

}