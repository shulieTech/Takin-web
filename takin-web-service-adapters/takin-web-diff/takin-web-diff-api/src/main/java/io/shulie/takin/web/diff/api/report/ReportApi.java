package io.shulie.takin.web.diff.api.report;

import java.util.List;

import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author 无涯
 * @date 2020/12/17 1:06 下午
 */
public interface ReportApi {

    /**
     * 根据租户查询报告数据
     * @param req
     * @return
     */
    ResponseResult<List<Long>> queryListRunningReport(CloudCommonInfoWrapperReq req);

    /**
     * 根据报告id获取报告详情
     *
     * @return
     */
    ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req);

    /**
     * 根据场景id获取报告详情
     *
     * @return
     */
    ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req);

}
