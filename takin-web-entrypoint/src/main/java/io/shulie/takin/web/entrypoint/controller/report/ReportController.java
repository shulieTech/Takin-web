package io.shulie.takin.web.entrypoint.controller.report;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.sdk.model.request.report.TrendRequest;
import io.shulie.takin.cloud.sdk.model.request.report.WarnQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ActivityResponse;
import io.shulie.takin.cloud.sdk.model.response.report.TrendResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "场景报告模块")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("report/listReport")
    @ApiOperation("报告列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_REPORT,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ReportDTO> listReport(ReportQueryParam reportQuery) {
        return reportService.listReport(reportQuery);
    }

    @GetMapping(value = "report/getReportByReportId")
    @ApiOperation("报告详情")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    public ReportDetailOutput getReportByReportId(Long reportId) {
        return reportService.getReportByReportId(reportId);
    }

    @GetMapping("report/queryReportTrend")
    @ApiOperation("报告链路趋势")
    public TrendResponse queryReportTrend(TrendRequest reportTrendQuery) {
        return reportService.queryReportTrend(reportTrendQuery);
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
    public ReportDetailTempOutput tempReportDetail(Long sceneId) {
        return reportService.tempReportDetail(sceneId);
    }

    @GetMapping("/report/queryTempReportTrend")
    @ApiOperation("实况报告链路趋势")
    public TrendResponse queryTempReportTrend(TrendRequest reportTrendQuery) {
        return reportService.queryTempReportTrend(reportTrendQuery);
    }

    @GetMapping("/report/listWarn")
    @ApiOperation("警告列表")
    public List<WarnDetailResponse> listWarn(WarnQueryReq param) {
        return reportService.listWarn(param);
    }

    @GetMapping("/report/queryReportActivityByReportId")
    @ApiOperation("报告的业务活动")
    public List<ActivityResponse> queryReportActivityByReportId(Long reportId) {
        return reportService.queryReportActivityByReportId(reportId);
    }

    @GetMapping("/report/queryReportActivityBySceneId")
    @ApiOperation("报告的业务活动")
    public List<ActivityResponse> queryReportActivityBySceneId(Long sceneId) {
        return reportService.queryReportActivityBySceneId(sceneId);
    }

}