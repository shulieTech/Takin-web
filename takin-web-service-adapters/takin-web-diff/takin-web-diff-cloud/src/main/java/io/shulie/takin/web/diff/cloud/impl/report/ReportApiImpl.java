package io.shulie.takin.web.diff.cloud.impl.report;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.shulie.takin.web.diff.api.report.ReportApi;
import io.shulie.takin.cloud.open.api.report.CloudReportApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;

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

}
