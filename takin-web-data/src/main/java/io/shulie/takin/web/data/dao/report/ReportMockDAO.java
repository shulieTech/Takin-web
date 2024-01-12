package io.shulie.takin.web.data.dao.report;

import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.param.report.ReportMockCreateParam;
import io.shulie.takin.web.data.result.report.ReportMockResult;

import java.util.List;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: ReportSummaryDao
 * @Description: TODO
 * @Date: 2021/11/23 09:55
 */
public interface ReportMockDAO {

    /**
     * 插入
     * @param param
     * @return
     */
    void insertOrUpdate(ReportMockCreateParam param);

    Long selectCountMockByReportId(Long reportId);

    List<ReportMockResult> selectByExample(ReportLocalQueryParam queryParam);
}
