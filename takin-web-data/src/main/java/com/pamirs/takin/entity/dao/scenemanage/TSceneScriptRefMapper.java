package com.pamirs.takin.entity.dao.scenemanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.scenemanage.SceneScriptRef;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;
import org.apache.ibatis.annotations.Param;

public interface TSceneScriptRefMapper {

    int deleteByPrimaryKey(Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    Long insertSelective(SceneScriptRef record);

    void batchInsert(@Param("items") List<SceneScriptRef> records);

    SceneScriptRef selectByPrimaryKey(Long id);

    List<SceneScriptRef> selectBySceneIdAndScriptType(@Param("sceneId") Long sceneId,
        @Param("scriptType") Integer scriptType);

    int updateByPrimaryKeySelective(SceneScriptRef record);

    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);

}
