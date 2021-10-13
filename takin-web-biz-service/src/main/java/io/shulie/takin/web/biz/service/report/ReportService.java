package io.shulie.takin.web.biz.service.report;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取指标列表
     * time
     * avgTps
     * 两个 key
     *
     * @param reportId 报告 id
     * @param sceneId 场景 id
     * @param customerId 租户 id
     * @return 指标列表
     */
    List<Map<String, Object>> listMetrics(Long reportId, Long sceneId, Long customerId);

    WebResponse queryReportCount(Long reportId);

    WebResponse queryRunningReport();

    WebResponse queryListRunningReport();

    WebResponse lockReport(Long reportId);

    WebResponse unLockReport(Long reportId);

    WebResponse finishReport(Long reportId);

}
