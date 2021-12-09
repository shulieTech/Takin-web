package com.pamirs.takin.entity.dao.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SceneDto;
import com.pamirs.takin.entity.domain.entity.linkmanage.Scene;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.SceneQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TSceneMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Scene record);

    int insertSelective(Scene record);

    Scene selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Scene record);

    int updateByPrimaryKey(Scene record);

    //根据场景名集合将场景的状态该为ischange
    void updateBySceneIdList(@Param("list") List<Long> changeToZeroSceneNameList, @Param("ischange") Integer ischange);

    List<SceneDto> selectByRelatedQuery(@Param("vo") SceneQueryVo vo,@Param("userIds") List<Long> userIds);

    long count();

    long countByTime(java.util.Date date);

    int updateIsChangeByTechId(Long techLinkId);

    int valiteSceneName(@Param("sceneName") String sceneName);

    List<BusinessFlowIdAndNameDto> businessFlowIdFuzzSearch(@Param("businessFlowName") String businessFlowName);

    /**
     * 根据id列表查询业务流程名称
     * @param ids
     * @return
     */
    List<Scene> selectBusinessFlowNameByIds(@Param("list") List<Long> ids);
}
