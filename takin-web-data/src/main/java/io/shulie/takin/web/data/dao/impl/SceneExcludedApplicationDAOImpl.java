package io.shulie.takin.web.data.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.web.data.dao.SceneExcludedApplicationDAO;
import io.shulie.takin.web.data.mapper.mysql.SceneExcludedApplicationMapper;
import io.shulie.takin.web.data.model.mysql.SceneExcludedApplicationEntity;
import io.shulie.takin.web.data.param.CreateSceneExcludedApplicationParam;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 探针包表(SceneExcludedApplication)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-10-28 16:21:54
 */
@Service
public class SceneExcludedApplicationDAOImpl
    implements SceneExcludedApplicationDAO, MPUtil<SceneExcludedApplicationEntity> {

    @Autowired
    private SceneExcludedApplicationMapper sceneExcludedApplicationMapper;

    @Override
    public boolean saveBatch(List<CreateSceneExcludedApplicationParam> createSceneExcludedApplicationParams) {
        return SqlHelper.retBool(sceneExcludedApplicationMapper.insertBatch(
            createSceneExcludedApplicationParams));
    }

    @Override
    public List<Long> listApplicationIdsBySceneId(Long sceneId) {
        return sceneExcludedApplicationMapper.selectObjs(this.getLambdaQueryWrapper()
            .select(SceneExcludedApplicationEntity::getApplicationId)
            .eq(SceneExcludedApplicationEntity::getSceneId, sceneId)).stream()
            .map(obj -> Long.valueOf(obj.toString())).collect(Collectors.toList());
    }

    @Override
    public void removeBySceneId(Long sceneId) {
        sceneExcludedApplicationMapper.delete(this.getLambdaQueryWrapper().eq(SceneExcludedApplicationEntity::getSceneId, sceneId));
    }

}

