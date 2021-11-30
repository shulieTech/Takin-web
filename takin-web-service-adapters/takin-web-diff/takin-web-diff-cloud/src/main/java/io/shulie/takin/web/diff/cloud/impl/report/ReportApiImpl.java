package io.shulie.takin.web.diff.cloud.impl.report;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.stereotype.Service;

import io.shulie.takin.web.diff.api.report.ReportApi;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;

/**
 * @author 无涯
 * @date 2020/12/17 1:10 下午
 */
@Service
public class ReportApiImpl implements ReportApi {

    @Resource(type = CloudReportApi.class)
    private CloudReportApi cloudReportApi;

    @Override
    public ResponseResult<List<Long>> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        try {
            return ResponseResult.success(cloudReportApi.queryListRunningReport(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req) {
        try {
            return ResponseResult.success(cloudReportApi.getReportByReportId(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req) {
        try {
            return ResponseResult.success(cloudReportApi.tempReportDetail(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    @Override
    public ResponseResult<List<ScriptNodeTreeResp>> scriptNodeTree(ScriptNodeTreeQueryReq req) {
        return cloudReportApi.queryNodeTree(req);
    }

    @Override
    public ResponseResult<ReportTrendResp> tmpReportTrend(ReportTrendQueryReq req) {
        //return cloudReportApi.tempTrend(req);
        // TODO 调整SDK
        return null;
    }

    @Override
    public ResponseResult<ReportTrendResp> reportTrend(ReportTrendQueryReq req) {
        //return cloudReportApi.queryReportTrend(req);
        // TODO 调整SDK
        return null;
    }

    @Override
    public ResponseResult<NodeTreeSummaryResp> getSummaryList(Long reportId) {
        ReportDetailByIdReq request = new ReportDetailByIdReq() {{setReportId(reportId);}};
        WebPluginUtils.fillCloudUserData(request);
        return cloudReportApi.getSummaryList(request);
    }

}
