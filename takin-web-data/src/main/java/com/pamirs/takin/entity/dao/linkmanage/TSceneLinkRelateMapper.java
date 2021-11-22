package com.pamirs.takin.entity.dao.linkmanage;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.pamirs.takin.entity.domain.entity.linkmanage.SceneLinkRelate;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TSceneLinkRelateMapper {

    @InterceptorIgnore(tenantLine = "true")
    int batchInsert(@Param("list") List<SceneLinkRelate> records);

    @InterceptorIgnore(tenantLine = "true")
    List<BusinessFlowTree> findAllRecursion(@Param("sceneId") String sceneId,@Param("tenantId") Long tenantId,@Param("envCode") String envCode);

}
