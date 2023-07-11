package com.pamirs.takin.entity.dao.scenemanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.scenemanage.SceneBusinessActivityRef;
import org.apache.ibatis.annotations.Param;

public interface TSceneBusinessActivityRefMapper {

    int deleteByPrimaryKey(Long id);

    int deleteBySceneId(Long sceneId);

    Long insertSelective(SceneBusinessActivityRef record);

    void batchInsert(@Param("items") List<SceneBusinessActivityRef> records);

    SceneBusinessActivityRef selectByPrimaryKey(Long id);

    List<SceneBusinessActivityRef> selectBySceneId(Long sceneId);

    int updateByPrimaryKeySelective(SceneBusinessActivityRef record);

    SceneBusinessActivityRef querySceneBusinessActivityRefByActivityId(@Param("sceneId") Long sceneId,
        @Param("businessActivityId") Long businessActivityId);
}
