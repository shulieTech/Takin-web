package io.shulie.takin.web.diff.api.report;

import java.util.List;

import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportDetailResp;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @author 无涯
 * @date 2020/12/17 1:06 下午
 */
public interface ReportApi {

    /**
     * 根据租户查询报告数据
     *
     * @param req -
     * @return -
     */
    ResponseResult<List<Long>> queryListRunningReport(CloudCommonInfoWrapperReq req);

    /**
     * 根据报告id获取报告详情
     *
     * @param req -
     * @return -
     */
    ResponseResult<ReportDetailResp> getReportByReportId(ReportDetailByIdReq req);

    /**
     * 根据场景id获取报告详情
     *
     * @param req -
     * @return -
     */
    ResponseResult<ReportDetailResp> tempReportDetail(ReportDetailBySceneIdReq req);

    /**
     * 查询报告对应的脚本节点树
     *
     * @param req 请求参数
     * @return -
     */
    List<ScriptNodeTreeResp> scriptNodeTree(ScriptNodeTreeQueryReq req);

    /**
     * 查询报告实况趋势
     *
     * @param req 请求参数
     * @return -
     */
    ReportTrendResp tmpReportTrend(ReportTrendQueryReq req);

    /**
     * 查询报告趋势
     *
     * @param req 请求参数
     * @return -
     */
    ReportTrendResp reportTrend(ReportTrendQueryReq req);

    /**
     * 查询报告详情
     *
     * @param reportId 报告ID
     * @return -
     */
    NodeTreeSummaryResp getSummaryList(Long reportId);


    /**
     * 获取下载Jml地址
     * @param reportId
     * @return
     */
    String getJtlDownLoadUrl(Long reportId);


}
