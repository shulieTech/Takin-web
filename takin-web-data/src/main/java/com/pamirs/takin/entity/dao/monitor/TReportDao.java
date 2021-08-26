package com.pamirs.takin.entity.dao.monitor;

import java.util.List;

import com.pamirs.takin.entity.dao.common.BaseDao;
import com.pamirs.takin.entity.domain.entity.TReport;
import com.pamirs.takin.entity.domain.query.TReportQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 压测报告查询dao
 */
@Mapper
public interface TReportDao extends BaseDao<TReport> {

    /**
     * 查询列表
     *
     * @param query
     * @return
     */
    List<TReport> selectList(TReportQuery query);

    /**
     * 查询列表数
     *
     * @param query
     * @return
     */
    long selectListCount(TReportQuery query);

    /**
     * 按压测场景和状态查询
     *
     * @param id
     * @param status
     * @return
     */
    TReport selectScenarioId(@Param("scenarioId") Long id, @Param("status") Integer status);

    /**
     * 说明: 查询正在测试的二级链路 ,更新它的状态为停止
     *
     * @param secondLinkId 二级链路id
     * @param status       压测的二级链路状态
     * @return 压测报告集合
     * @author shulie
     * @date 2018/7/11 16:14
     */
    List<TReport> queryBySecondLinkIdAndStatus(@Param("secondLinkId") String secondLinkId,
        @Param("status") String status);

    List<TReport> queryByStatus(String status);
}
