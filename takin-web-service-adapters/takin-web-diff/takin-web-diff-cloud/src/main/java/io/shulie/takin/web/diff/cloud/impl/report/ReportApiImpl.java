package io.shulie.takin.web.diff.cloud.impl.report;

import java.util.List;

import io.shulie.takin.cloud.open.api.report.CloudReportApi;
import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
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
    public ResponseResult<List<Long>> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        return cloudReportApi.queryListRunningReport(req);
    }

    @Override
    public ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req) {
        return cloudReportApi.getReportByReportId(req);
    }

    @Override
    public ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req) {
        return cloudReportApi.tempReportDetail(req);
    }

}
