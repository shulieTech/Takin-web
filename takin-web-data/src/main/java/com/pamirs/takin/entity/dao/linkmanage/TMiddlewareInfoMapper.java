package com.pamirs.takin.entity.dao.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkRemarkmiddleWareDto;
import com.pamirs.takin.entity.domain.entity.linkmanage.TMiddlewareInfo;
import com.pamirs.takin.entity.domain.entity.linkmanage.statistics.StatisticsQueryVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import org.apache.ibatis.annotations.Param;

public interface TMiddlewareInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TMiddlewareInfo record);

    int insertSelective(TMiddlewareInfo record);

    TMiddlewareInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TMiddlewareInfo record);

    int updateByPrimaryKey(TMiddlewareInfo record);

    List<TMiddlewareInfo> selectBySelective(TMiddlewareInfo info);

    List<TMiddlewareInfo> selectBySystemProcessId(@Param("systemProcessId") Long systemProcessId);

    /**
     * 统计页面中间件信息连表查询
     *
     * @param vo
     * @return
     */
    List<LinkRemarkmiddleWareDto> selectforstatistics(StatisticsQueryVo vo);

    List<MiddleWareEntity> selectByIds(@Param("list") List<Long> midllewareIdslong);
}
