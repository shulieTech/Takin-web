package com.pamirs.takin.entity.dao.linkmanage;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.pamirs.takin.entity.domain.entity.linkmanage.SceneLinkRelate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TSceneLinkRelateMapper {

    @InterceptorIgnore(tenantLine = "true")
    int batchInsert(@Param("list") List<SceneLinkRelate> records);

    List<SceneLinkRelate> selectBySceneId(@Param("sceneId") Long sceneId);

}
