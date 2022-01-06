package io.shulie.takin.web.data.dao;

import java.util.List;

import io.shulie.takin.web.data.param.CreateSceneExcludedApplicationParam;

/**
 * 探针包表(SceneExcludedApplication)表数据库 dao 层
 *
 * @author liuchuan
 * @date 2021-10-28 16:21:54
 */
public interface SceneExcludedApplicationDAO {

    /**
     * 批量创建
     *
     * @param createSceneExcludedApplicationParams 入参
     * @return 是否成功
     */
    boolean saveBatch(List<CreateSceneExcludedApplicationParam> createSceneExcludedApplicationParams);

    /**
     * 通过场景id 获得排除的应用ids
     *
     * @param sceneId 场景id
     * @return 应用ids
     */
    List<Long> listApplicationIdsBySceneId(Long sceneId);

    /**
     * 删除场景对应的忽略应用
     *
     * @param sceneId 场景id
     */
    void removeBySceneId(Long sceneId);

}

