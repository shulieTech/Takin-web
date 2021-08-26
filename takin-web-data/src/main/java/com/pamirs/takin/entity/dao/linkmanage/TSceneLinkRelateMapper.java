package com.pamirs.takin.entity.dao.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.linkmanage.SceneAndBusinessLink;
import com.pamirs.takin.entity.domain.entity.linkmanage.SceneLinkRelate;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TSceneLinkRelateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SceneLinkRelate record);

    int batchInsert(@Param("list") List<SceneLinkRelate> records);

    int insertSelective(SceneLinkRelate record);

    SceneLinkRelate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SceneLinkRelate record);

    int updateByPrimaryKey(SceneLinkRelate record);

    //根据场景名删除
    int deleteBySceneId(@Param("sceneId") String sceneName);

    List<SceneAndBusinessLink> selectSceneIdByTechLinkId(@Param("techLinkId") String techLinkId);

    List<SceneLinkRelate> selectBySceneId(@Param("sceneId") Long sceneId);

    int updateEntranceNameBySystemProcessId(@Param("linkId") String linkId, @Param("newEntrance") String newEntrance);

    List<String> selectBusinessIdByParentBusinessId(@Param("parentBusinessId") List<String> parentBusinessId);

    List<BusinessFlowTree> findAllRecursion(@Param("sceneId") String sceneId);

    int countBySceneId(@Param("sceneId") Long sceneId);

    int countByTechLinkIds(@Param("list") List<String> techLinkIds);

    long countByBusinessLinkId(@Param("businessLinkId") Long businessLinkId);
}
