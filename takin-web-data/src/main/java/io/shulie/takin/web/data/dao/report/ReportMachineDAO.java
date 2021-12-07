package io.shulie.takin.web.data.dao.report;

import java.util.List;
import java.util.Map;

import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.param.report.ReportMachineUpdateParam;
import io.shulie.takin.web.data.result.report.ReportMachineResult;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: ReportMachineDao
 * @Description: TODO
 * @Date: 2021/11/23 10:13
 */
public interface ReportMachineDAO {

    /**
     * 插入
     * @param param
     */
    void insert(ReportMachineUpdateParam param);


    /**
     * 插入并更新
     * @param param
     * @return
     */
    void insertOrUpdate(ReportMachineUpdateParam param);
    /**
     * 查询报告机器数据
     * @param queryParam
     * @return
     */
    List<ReportMachineResult> selectSimpleByExample(ReportLocalQueryParam queryParam);

    /**
     * 指定报告、应用、ip查询
     * @param queryParam
     * @return
     */
    ReportMachineResult selectOneByParam(ReportLocalQueryParam queryParam);

    /**
     * 指定报告、应用，查所有ip
     * @param queryParam
     * @return
     */
    List<ReportMachineResult> selectListByParam(ReportLocalQueryParam queryParam);

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
     * @param param
     * @return
     */
    int updateTpsTargetConfig(ReportMachineUpdateParam param);

    /**
     * 更新机器风险数据
     *
     * @param param
     * @return
     */
    int updateRiskContent(ReportMachineUpdateParam param);

    /**
     * 根据报告删除
     * @param reportId
     */
    void deleteByReportId(Long reportId);

}
