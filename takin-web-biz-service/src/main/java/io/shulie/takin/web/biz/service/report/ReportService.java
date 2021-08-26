package io.shulie.takin.web.biz.service.report;

import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import com.pamirs.takin.entity.domain.vo.sla.WarnQueryParam;
import io.shulie.takin.web.common.domain.WebResponse;

/**
 * @author qianshui
 * @date 2020/5/12 下午3:33
 */
public interface ReportService {

    WebResponse listReport(ReportQueryParam param);

    WebResponse getReportByReportId(Long reportId);

    WebResponse queryReportTrend(ReportTrendQueryParam param);

    WebResponse tempReportDetail(Long sceneId);

    WebResponse queryTempReportTrend(ReportTrendQueryParam param);

    WebResponse listWarn(WarnQueryParam param);

    WebResponse queryReportActivityByReportId(Long reportId);

    WebResponse queryReportActivityBySceneId(Long sceneId);

    WebResponse querySummaryList(Long reportId);

    WebResponse queryMetrices(Long reportId, Long sceneId, Long customerId);

    WebResponse queryReportCount(Long reportId);

    WebResponse queryRunningReport();

    WebResponse queryListRunningReport();

    WebResponse lockReport(Long reportId);

    WebResponse unLockReport(Long reportId);

    WebResponse finishReport(Long reportId);

}
