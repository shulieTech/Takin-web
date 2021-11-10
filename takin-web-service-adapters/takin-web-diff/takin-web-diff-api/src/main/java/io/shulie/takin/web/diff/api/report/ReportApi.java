package io.shulie.takin.web.diff.api.report;

import java.util.List;

import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.req.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.open.req.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.resp.report.ReportTrendResp;
import io.shulie.takin.cloud.open.resp.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author 无涯
 * @date 2020/12/17 1:06 下午
 */
public interface ReportApi {

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

    /**
     * 查询报告对应的脚本节点树
     * @param req 请求参数
     * @return
     */
    ResponseResult<List<ScriptNodeTreeResp>> scriptNodeTree(ScriptNodeTreeQueryReq req);


    ResponseResult<ReportTrendResp> tmpReportTrend(ReportTrendQueryReq req);

    ResponseResult<ReportTrendResp> reportTrend(ReportTrendQueryReq req);

}
