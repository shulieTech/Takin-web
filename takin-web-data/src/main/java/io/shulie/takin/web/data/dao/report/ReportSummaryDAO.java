package io.shulie.takin.web.data.dao.report;

import io.shulie.takin.web.data.param.report.ReportSummaryCreateParam;
import io.shulie.takin.web.data.result.report.ReportSummaryResult;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: ReportSummaryDao
 * @Description: TODO
 * @Date: 2021/11/23 09:55
 */
public interface ReportSummaryDAO {

    /**
     * 插入
     * @param param
     * @return
     */
    void insert(ReportSummaryCreateParam param);

    /**
     * 根据报告id查询
     * @param reportId
     * @return
     */
    ReportSummaryResult selectOneByReportId(Long reportId);

    /**
     * 根据报告id查询数据
     * @param reportId
     */
    void deleteByReportId(Long reportId);
}
