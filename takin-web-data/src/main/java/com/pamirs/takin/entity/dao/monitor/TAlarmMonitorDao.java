package com.pamirs.takin.entity.dao.monitor;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.TAlarmMonitorVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 说明: 监控告警dao
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/6/28 15:55
 */
@Mapper
public interface TAlarmMonitorDao {

    /**
     * 说明: 根据二级链路id查询告警列表
     *
     * @param secondLinkId 二级链路id
     * @param startTime    告警开始时间
     * @param endTime      告警结束时间
     * @return 二级链路下的告警列表
     * @author shulie
     * @date 2018/6/28 16:37
     */
    List<TAlarmMonitorVo> queryAlarmListBySecondLinkId(@Param("secondLinkId") String secondLinkId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime);
}
