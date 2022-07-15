package io.shulie.takin.web.entrypoint.controller.report;

import java.util.List;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.WarnQueryReq;
import io.shulie.takin.adapter.api.model.response.report.ActivityResponse;
import io.shulie.takin.adapter.api.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportJtlDownloadOutput;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.report.ReportJtlDownloadResponse;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "场景报告模块")
public class ReportController {

    @Resource
    private ReportService reportService;

    @GetMapping("report/listReport")
    @ApiOperation("报告列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_REPORT,
        needAuth = ActionTypeEnum.QUERY
    )
    public Response<List<ReportDTO>> listReport(ReportQueryParam reportQuery) {
        ResponseResult<List<ReportDTO>> response = reportService.listReport(reportQuery);
        return Response.success(response.getData(), response.getTotalNum());
    }

    /**
     * 报告列表无权限
     *
     * @param reportQuery 查询参数
     * @return 查询结果
     */
    @GetMapping("report/listReport/un_safe")
    @ApiOperation("大盘使用报告列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.DASHBOARD_REPORT,
        needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<List<ReportDTO>> listReportNoAuth(ReportQueryParam reportQuery) {
        ResponseResult<List<ReportDTO>> response = reportService.listReport(reportQuery);
        return ResponseResult.success(response.getData(), response.getTotalNum());
    }

    @GetMapping(value = "report/getReportByReportId")
    @ApiOperation("报告详情")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    public ResponseResult<ReportDetailOutput> getReportByReportId(Long reportId) {
        return ResponseResult.success(reportService.getReportByReportId(reportId));
    }

    /**
     * 实况报表
     */
    @GetMapping("report/tempReportDetail")
    @ApiOperation("实况报告")
    @ApiImplicitParam(name = "sceneId", value = "场景ID")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<ReportDetailTempOutput> tempReportDetail(Long sceneId) {
        return ResponseResult.success(reportService.tempReportDetail(sceneId));
    }

    @GetMapping("/report/queryTempReportTrend")
    @ApiOperation("实况报告链路趋势")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryReq reportTrendQuery) {
        return ResponseResult.success(reportService.queryTempReportTrend(reportTrendQuery));
    }

    @GetMapping("report/queryReportTrend")
    @ApiOperation("报告链路趋势")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq reportTrendQuery) {
        return ResponseResult.success(reportService.queryReportTrend(reportTrendQuery));
    }

    @GetMapping("/report/queryNodeTree")
    @ApiOperation("脚本节点树")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ReportQueryRequest request) {
        return reportService.queryNodeTree(request);
    }

    @GetMapping("/report/listWarn")
    @ApiOperation("警告列表")
    public Response<List<WarnDetailResponse>> listWarn(WarnQueryReq param) {
        ResponseResult<List<WarnDetailResponse>> response = reportService.listWarn(param);
        return Response.success(response.getData(), response.getTotalNum());
    }

    @GetMapping("/report/queryReportActivityByReportId")
    @ApiOperation("报告的业务活动")
    public ResponseResult<List<ActivityResponse>> queryReportActivityByReportId(Long reportId) {
        return ResponseResult.success(reportService.queryReportActivityByReportId(reportId));
    }

    @GetMapping("/report/queryReportActivityBySceneId")
    @ApiOperation("报告的业务活动")
    public ResponseResult<List<ActivityResponse>> queryReportActivityBySceneId(Long sceneId) {
        return ResponseResult.success(reportService.queryReportActivityBySceneId(sceneId));
    }

    @GetMapping("/report/businessActivity/summary/list")
    @ApiOperation("压测明细")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<NodeTreeSummaryResp> getSummaryList(Long reportId) {
        return ResponseResult.success(reportService.querySummaryList(reportId));
    }

    @GetMapping("/report/getJtlDownLoadUrl")
    @ApiOperation(value = "获取jtl文件下载路径")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_REPORT,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ReportJtlDownloadResponse getJtlDownLoadUrl(@RequestParam("reportId") Long reportId) {
        ReportJtlDownloadOutput output = reportService.getJtlDownLoadUrl(reportId);
        ReportJtlDownloadResponse response = new ReportJtlDownloadResponse();
        BeanUtils.copyProperties(output,response);
        return response;
    }

    @GetMapping("/report/export")
    @ApiOperation("导出压测报告pdf")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
            needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ResponseResult<String> getExportDownLoadUrl(Long reportId) {
        return ResponseResult.success(reportService.downloadPDFPath(reportId));
    }

    @GetMapping("/reportId")
    @ApiOperation("reportId")
    public ResponseResult<Long> reportId(Long sceneId) {
        return ResponseResult.success(reportService.getReportId(sceneId));
    }

}