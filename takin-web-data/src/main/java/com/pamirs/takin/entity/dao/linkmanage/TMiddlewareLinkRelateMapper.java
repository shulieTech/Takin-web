package com.pamirs.takin.entity.dao.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.linkmanage.MiddlewareLinkRelate;
import org.apache.ibatis.annotations.Param;

public interface TMiddlewareLinkRelateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiddlewareLinkRelate record);

    int batchInsert(@Param("lists") List<MiddlewareLinkRelate> records);

    int insertSelective(MiddlewareLinkRelate record);

    MiddlewareLinkRelate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiddlewareLinkRelate record);

    int updateByPrimaryKey(MiddlewareLinkRelate record);

    int deleteByTechLinkId(@Param("linkId") String linkId);

    List<String> selectMiddleWareIdsByTechIds(@Param("list") List<String> techIds);

    List<String> selectTechIdsByMiddleWareIds(@Param("middleWareId") Long middleWareId);
}
