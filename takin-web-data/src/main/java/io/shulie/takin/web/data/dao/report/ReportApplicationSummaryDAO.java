package io.shulie.takin.web.data.dao.report;

import java.util.List;
import java.util.Map;

import io.shulie.takin.web.data.param.report.ReportApplicationSummaryCreateParam;
import io.shulie.takin.web.data.param.report.ReportApplicationSummaryQueryParam;
import io.shulie.takin.web.data.result.report.ReportApplicationSummaryResult;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.result.report
 * @ClassName: ReportApplicationSummaryDAO
 * @Description: TODO
 * @Date: 2021/11/23 11:01
 */
public interface ReportApplicationSummaryDAO {

    /**
     * 插入或更新
     * @param param
     * @return
     */
    void insertOrUpdate(ReportApplicationSummaryCreateParam param);

    /**
     * 查询
     * @param param
     * @return
     */
    List<ReportApplicationSummaryResult> selectByParam(ReportApplicationSummaryQueryParam param);

    /**
     * 根据报告查询
     * @param reportId
     * @return
     */
    Map<String, Object> selectCountByReportId(Long reportId);

    /**
     * 删除
     * @param reportId
     */
    void deleteByReportId(Long reportId);
}
