package com.pamirs.takin.entity.dao.linkmanage;

import java.util.Date;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.TechLinkDto;
import com.pamirs.takin.entity.domain.entity.linkmanage.LinkManageTable;
import com.pamirs.takin.entity.domain.entity.linkmanage.LinkQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TLinkManageTableMapper {
    int deleteByPrimaryKey(Long linkId);

    int logicDeleteByPrimaryKey(@Param("linkId") Long linkId);

    int insert(LinkManageTable record);

    int insertSelective(LinkManageTable record);

    LinkManageTable selectByPrimaryKey(Long linkId);

    int updateByPrimaryKeySelective(LinkManageTable record);

    int updateByPrimaryKey(LinkManageTable record);

    //条件查询
    List<LinkManageTable> selectBySelective(LinkManageTable table);

    //条件查询
    List<TechLinkDto> selectTechLinkListBySelective2(@Param("queryVo")LinkQueryVo queryVo,@Param("userIds") List<Long> userIds);

    TechLinkDto selectTechLinkById(@Param("linkId") Long linkId);

    int counItemtByTechLinkIds(@Param("linkIds") List<Long> linkIds);

    //校验链路名不能重复
    int count(@Param("linkName") String linkName);

    int countByEntrance(@Param("entrance") String entrance);

    //统计系统流程的总数
    long countTotal();

    //统计系统流程变更的数量
    long countChangeNum();

    //统计应用数量
    long countApplication();

    long countSystemProcessByTime(Date date);

    long countApplicationByTime(Date date);

    long cannotdelete(@Param("linkId") Long linkId, @Param("canDelelte") Long canDelelte);

    /**
     * 模糊查询所有入口
     *
     * @param entrance
     * @return
     */
    List<String> entranceFuzzSerach(@Param("entrance") String entrance);

    /**
     * 业务活动id查询系统流程
     *
     * @param id
     * @return
     */
    List<LinkManageTable> selectByBussinessId(@Param("id") Long id);
}
