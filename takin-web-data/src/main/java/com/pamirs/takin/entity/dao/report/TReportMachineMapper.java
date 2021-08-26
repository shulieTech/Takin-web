package com.pamirs.takin.entity.dao.report;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.report.ReportMachine;
import com.pamirs.takin.entity.domain.entity.report.TakinTraceEntry;
import com.pamirs.takin.entity.domain.vo.report.ReportLocalQueryParam;
import org.apache.ibatis.annotations.Param;

public interface TReportMachineMapper {

    int insert(ReportMachine record);

    int insertOrUpdate(ReportMachine record);

    int insertList(List<ReportMachine> list);

    List<ReportMachine> selectSimpleByExample(ReportLocalQueryParam queryParam);

    ReportMachine selectByPrimaryKey(Long id);

    /**
     * 指定报告、应用、ip查询
     *
     * @param queryParam
     * @return
     */
    ReportMachine selectOneByParam(ReportLocalQueryParam queryParam);

    /**
     * 指定报告、应用，查所有ip
     *
     * @param queryParam
     * @return
     */
    List<ReportMachine> selectListByParam(ReportLocalQueryParam queryParam);

    /**
     * 分应用数、汇总总机器数、风险机器数
     *
     * @param reportId
     * @return
     */
    List<Map<String, Object>> selectCountByReport(Long reportId);

    /**
     * 更新机器tps指标数据
     *
     * @param record
     * @return
     */
    int updateTpsTargetConfig(ReportMachine record);

    /**
     * 更新机器风险数据
     *
     * @param record
     * @return
     */
    int updateRiskContent(ReportMachine record);

    void deleteByReportId(Long reportId);

    Long getTraceFailedCount(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<TakinTraceEntry> selectTraceByTimeRange(@Param("startTime") long startTime, @Param("endTime") long endTime,
        @Param("type") Integer type, @Param("list") List<String> entryList);
}
