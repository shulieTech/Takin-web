package com.pamirs.takin.cloud.entity.dao.report;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import org.apache.ibatis.annotations.Param;

/**
 * @author -
 */
public interface TReportBusinessActivityDetailMapper {

    /**
     * 插入
     *
     * @param record -
     * @return -
     */
    int insertSelective(ReportBusinessActivityDetail record);

    /**
     * 更新
     *
     * @param record -
     * @return -
     */
    int updateByPrimaryKeySelective(ReportBusinessActivityDetail record);

    /**
     * 根据主键查询
     *
     * @param id 数据主键
     * @return -
     */
    ReportBusinessActivityDetail selectByPrimaryKey(Long id);

    /**
     * 查询报告关联的业务活动详情
     *
     * @param reportId 报告主键
     * @return -
     */
    List<ReportBusinessActivityDetail> queryReportBusinessActivityDetailByReportId(@Param("reportId") Long reportId);

    /**
     * 根据报告主键查询关联的业务活动总数
     *
     * @param reportId 报告主键
     * @return 报告主键-总数
     */
    Map<String, Object> selectCountByReportId(Long reportId);
}
