package io.shulie.takin.web.diff.cloud.impl.report;

import java.util.List;

import io.shulie.takin.cloud.open.api.report.CloudReportApi;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.req.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.open.req.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.open.resp.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.resp.report.ReportTrendResp;
import io.shulie.takin.cloud.open.resp.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.diff.api.report.ReportApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/12/17 1:10 下午
 */
@Service
public class ReportApiImpl implements ReportApi {

    @Autowired
    private CloudReportApi cloudReportApi;


    @Override
    public ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req) {
        return cloudReportApi.getReportByReportId(req);
    }

    @Override
    public ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req) {
        return cloudReportApi.tempReportDetail(req);
    }

    @Override
    public ResponseResult<List<ScriptNodeTreeResp>> scriptNodeTree(ScriptNodeTreeQueryReq req) {
        return cloudReportApi.queryNodeTree(req);
    }

    @Override
    public ResponseResult<ReportTrendResp> tmpReportTrend(ReportTrendQueryReq req) {
        return cloudReportApi.queryTempReportTrend(req);
    }

    @Override
    public ResponseResult<ReportTrendResp> reportTrend(ReportTrendQueryReq req) {
        return cloudReportApi.queryReportTrend(req);
    }

    @Override
    public ResponseResult<NodeTreeSummaryResp> getBusinessActivitySummaryList(Long reportId){
        return cloudReportApi.getBusinessActivitySummaryList(new ReportDetailByIdReq(){{setReportId(reportId);}});
    }

}
