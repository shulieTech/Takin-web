package io.shulie.takin.web.entrypoint.controller.openapi;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.report.MachineDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.sdk.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.sdk.model.request.report.TrendRequest;
import io.shulie.takin.cloud.sdk.model.request.report.WarnQueryReq;
import io.shulie.takin.cloud.sdk.model.response.report.ActivityResponse;
import io.shulie.takin.cloud.sdk.model.response.report.ReportTrendResp;
import io.shulie.takin.cloud.sdk.model.response.report.TrendResponse;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.report.ReportLocalService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.biz.pojo.openapi.converter.ReportOpenApiConverter;
import io.shulie.takin.web.biz.pojo.openapi.request.report.ReportQueryOpenApiReq;
import io.shulie.takin.web.biz.pojo.openapi.response.report.BusinessActivityOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.report.ReportDetailOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.report.ReportOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.report.ReportTrendOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.report.WarnDetailOpenApiResp;
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
@RequestMapping(ApiUrls.TAKIN_OPEN_API_URL)
@Api(tags = "场景报告模块")
public class ReportOpenApi {

    @Autowired
    private ReportService reportService;
    @Autowired
    private ReportLocalService reportLocalService;

    @GetMapping("report/listReport")
    @ApiOperation("报告列表")
    public Response<List<ReportOpenApiResp>> listReport(ReportQueryOpenApiReq reportQueryOpenApiReq) {
        ReportQueryParam reportQuery = ReportOpenApiConverter.INSTANCE.ofReportQueryOpenApiReq(reportQueryOpenApiReq);
        ResponseResult<List<ReportDTO>> webResponse = reportService.listReport(reportQuery);
        return Response.success(ofListReportOpenApiResp(webResponse.getData()), webResponse.getTotalNum());
    }

    @GetMapping(value = "report/getReportByReportId")
    @ApiOperation("报告详情")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    public Response<ReportDetailOpenApiResp> getReportByReportId(Long reportId) {
        ReportDetailOutput reportByReportId = reportService.getReportByReportId(reportId);
        return Response.success(ofReportDetailOpenApiResp(reportByReportId));
    }

    @GetMapping("report/queryReportTrend")
    @ApiOperation("报告链路趋势")
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq request) {
        ReportTrendResp webResponse = reportService.queryReportTrend(request);
        return ResponseResult.success(webResponse);
    }

    @GetMapping("/report/listWarn")
    @ApiOperation("警告列表")
    public Response<List<WarnDetailOpenApiResp>> listWarn(WarnQueryReq req) {
        ResponseResult<List<WarnDetailResponse>> webResponse = reportService.listWarn(req);
        return Response.success(ofListWarnDetailOpenApiResp(webResponse.getData()), webResponse.getTotalNum());
    }

    @GetMapping("/report/queryReportActivityByReportId")
    @ApiOperation("报告的业务活动")
    public Response<List<BusinessActivityOpenApiResp>> queryReportActivityByReportId(Long reportId) {
        List<ActivityResponse> webResponse = reportService.queryReportActivityByReportId(reportId);
        return Response.success(ReportOpenApiConverter.INSTANCE.ofLsitBusinessActivityOpenApiResp(webResponse));
    }

    @GetMapping("/report/queryReportActivityBySceneId")
    @ApiOperation("报告的业务活动")
    public Response<List<BusinessActivityOpenApiResp>> queryReportActivityBySceneId(Long sceneId) {
        List<ActivityResponse> webResponse = reportService.queryReportActivityBySceneId(sceneId);
        return Response.success(ReportOpenApiConverter.INSTANCE.ofLsitBusinessActivityOpenApiResp(webResponse));
    }

    /**
     * 详情
     *
     * @return -
     */
    @GetMapping("/report/machine/detail")
    @ApiOperation("性能详情")
    public MachineDetailDTO getMachineDetail(Long reportId, String applicationName, String machineIp) {
        return reportLocalService.getMachineDetail(reportId, applicationName, machineIp);
    }

    private ReportDetailOpenApiResp ofReportDetailOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2Bean(s, ReportDetailOpenApiResp.class);
    }

    private ReportTrendOpenApiResp ofReportTrendOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2Bean(s, ReportTrendOpenApiResp.class);
    }

    private List<ReportOpenApiResp> ofListReportOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2List(s, ReportOpenApiResp.class);
    }

    private List<WarnDetailOpenApiResp> ofListWarnDetailOpenApiResp(Object data) {
        if (data == null) {
            return null;
        }
        String s = JsonHelper.bean2Json(data);
        return JsonHelper.json2List(s, WarnDetailOpenApiResp.class);
    }
}
