package io.shulie.takin.web.biz.service.report;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceQueryDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import com.pamirs.takin.entity.domain.vo.sla.WarnQueryParam;
import io.shulie.takin.cloud.sdk.model.common.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.request.report.TrendRequest;
import io.shulie.takin.cloud.sdk.model.request.report.WarnQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ActivityResponse;
import io.shulie.takin.cloud.sdk.model.response.report.MetricesResponse;
import io.shulie.takin.cloud.sdk.model.response.report.TrendResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.common.domain.WebResponse;

import java.util.List;
import java.util.Map;

/**
 * @author qianshui
 * @date 2020/5/12 下午3:33
 */
public interface ReportService {

    List<ReportDTO> listReport(ReportQueryParam param);

    ReportDetailOutput getReportByReportId(Long reportId);

    TrendResponse queryReportTrend(TrendRequest param);
    WebResponse queryReportTrend(ReportTrendQueryParam param);
    WebResponse queryReportTrendWithTopology(ReportTrendQueryParam reportTrendQuery);

    ReportDetailTempOutput tempReportDetail(Long sceneId);

    TrendResponse queryTempReportTrend(TrendRequest param);
    WebResponse queryTempReportTrend(ReportTrendQueryParam param);
    WebResponse queryTempReportTrendWithTopology(ReportTrendQueryParam reportTrendQuery,
                                                 ReportTraceQueryDTO queryDTO);

    List<WarnDetailResponse> listWarn(WarnQueryReq req);

    List<ActivityResponse> queryReportActivityByReportId(Long reportId);

    List<ActivityResponse> queryReportActivityBySceneId(Long sceneId);

    List<BusinessActivitySummaryBean> querySummaryList(Long reportId);

    /**
     * 获取指标列表
     * time
     * avgTps
     * 两个 key
     *
     * @param reportId   报告 id
     * @param sceneId    场景 id
     * @param customerId 租户 id
     * @return 指标列表
     */
    List<MetricesResponse> queryMetrics(Long reportId, Long sceneId, Long customerId);

    Map<String, Object> queryReportCount(Long reportId);

    /**
     * 查询运行中的报告
     *
     * @return 报告主键
     */
    Long queryRunningReport();

    /**
     * 获取正在压测的报告id
     *
     * @return 报告主键
     */
    List<Long> queryListRunningReport();

    Boolean lockReport(Long reportId);

    Boolean unLockReport(Long reportId);

    Boolean finishReport(Long reportId);

}
