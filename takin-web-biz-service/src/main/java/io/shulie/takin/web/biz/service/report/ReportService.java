package io.shulie.takin.web.biz.service.report;

import com.pamirs.takin.entity.domain.dto.report.ReportTraceQueryDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import com.pamirs.takin.entity.domain.vo.sla.WarnQueryParam;
import io.shulie.takin.cloud.open.resp.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.resp.report.ReportTrendResp;
import io.shulie.takin.cloud.open.resp.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;
import io.shulie.takin.web.common.domain.WebResponse;

import java.util.List;
import java.util.Map;

/**
 * @author qianshui
 * @date 2020/5/12 下午3:33
 */
public interface ReportService {

    WebResponse listReport(ReportQueryParam param);

    ResponseResult<ReportDetailOutput> getReportByReportId(Long reportId);

    ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryParam param);

    ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryParam param);

    //WebResponse queryReportTrendWithTopology(ReportTrendQueryParam reportTrendQuery);

    ResponseResult<ReportDetailTempOutput> tempReportDetail(Long sceneId);


    //WebResponse queryTempReportTrendWithTopology(ReportTrendQueryParam reportTrendQuery,ReportTraceQueryDTO queryDTO);

    WebResponse listWarn(WarnQueryParam param);

    WebResponse queryReportActivityByReportId(Long reportId);

    WebResponse queryReportActivityBySceneId(Long sceneId);

    ResponseResult<NodeTreeSummaryResp> querySummaryList(Long reportId);

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

    /**
     * 查询脚本节点树
     * @param request 查询参数
     * @return
     */
    ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ReportQueryRequest request);

}
