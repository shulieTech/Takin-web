package io.shulie.takin.web.entrypoint.controller.report;

import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.adapter.api.model.ScriptNodeSummaryBean;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.WarnQueryReq;
import io.shulie.takin.adapter.api.model.response.report.*;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.cloud.biz.utils.ReportLtDetailOutputUtils;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.output.report.*;
import io.shulie.takin.web.biz.pojo.request.pts.IdRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportLinkDiagramReq;
import io.shulie.takin.web.biz.pojo.request.report.ReportLinkDiagramReq2;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.report.ReportJtlDownloadResponse;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private ApplicationService applicationService;

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

    @PostMapping(value = "report/delete")
    @ApiOperation("删除报告")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_REPORT,
            needAuth = ActionTypeEnum.DELETE
    )
    public ResponseResult deleteReport(@RequestBody IdRequest idRequest) {
        reportService.deleteReport(idRequest.getId());
        return ResponseResult.success();
    }

    @GetMapping(value = "vlt/report/getReportById")
    @ApiOperation("LT版-压测报告基本信息")
    @ApiImplicitParam(name = "reportId", value = "报告ID")
    public ResponseResult<ReportLtDetailOutput> getReportById(Long reportId) {
        ReportDetailOutput output = reportService.getReportByReportId(reportId);
        ReportLtDetailOutput detailOutput = ReportLtDetailOutputUtils.convertToLt(output);
        //根据appId，查询appName
        if(CollectionUtils.isNotEmpty(detailOutput.getAppNames())) {
            List<Long> appIds = new ArrayList<>();
            for(String appName : detailOutput.getAppNames()) {
                appIds.add(Long.parseLong(appName));
            }
            List<ApplicationListResponse> responseList = applicationService.getApplicationListByAppIds(appIds);
            if(CollectionUtils.isNotEmpty(responseList)) {
                detailOutput.setAppNames(responseList.stream().map(ApplicationListResponse::getApplicationName).collect(Collectors.toList()));
                detailOutput.getAppNames().stream().sorted();
            }
        }
        //查找场景的其他报告
        List<SceneReportListOutput> scenReportList = reportService.getReportListBySceneId(output.getSceneId());
        if(CollectionUtils.isNotEmpty(scenReportList)) {
            //过滤本报告
            List<SceneReportListOutput> filterReportList = scenReportList.stream().filter(data -> data.getReportId() != reportId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(filterReportList)) {
                //按照时间倒排
                filterReportList.sort((o1, o2) -> {
                    if(o1.getReportId() > o2.getReportId()) {
                        return -1;
                    } else if(o1.getReportId() < o2.getReportId()) {
                        return 1;
                    }
                    return 0;
                });
                detailOutput.setReports(filterReportList);
            }
        }
        return ResponseResult.success(detailOutput);
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
        ReportTrendResp reportTrendResp = reportService.queryTempReportTrend(reportTrendQuery);
        formatReportTrendData(reportTrendResp);
        return ResponseResult.success(reportTrendResp);
    }

    @GetMapping("report/queryReportTrend")
    @ApiOperation("报告链路趋势")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryReq reportTrendQuery) {
        ReportTrendResp reportTrendResp = reportService.queryReportTrend(reportTrendQuery);
        formatReportTrendData(reportTrendResp);
        return ResponseResult.success(reportTrendResp);
    }

    @GetMapping("report/queryReportTrendByThread")
    @ApiOperation("线程报告链路趋势")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
            needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<ThreadReportTrendResp> queryReportTrendByThread(ReportTrendQueryReq reportTrendQuery) {
        return ResponseResult.success(reportService.queryReportTrendByThread(reportTrendQuery));
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

    /**
     * 获取指定阶梯递增线程组的指定线程数压测明细
     *
     * @param reportId
     * @param xpathMd5
     * @param threadNum
     * @return
     */
    @GetMapping("/report/businessActivity/summary/threadGroup")
    @ApiOperation("阶梯递增压测明细")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE,
            needAuth = ActionTypeEnum.START_STOP
    )
    public ResponseResult<ScriptNodeSummaryBean> getSummaryForThreadGroup(Long reportId, String xpathMd5, Double threadNum){
        if(threadNum == null){
            NodeTreeSummaryResp summaryResp = reportService.querySummaryList(reportId);
            ScriptNodeSummaryBean scriptNodeSummaryBean = summaryResp.getScriptNodeSummaryBeans().get(0).getChildren().stream().filter(bean -> xpathMd5.equals(bean.getXpathMd5())).findFirst().get();
            return ResponseResult.success(scriptNodeSummaryBean);
        }
        return ResponseResult.success(reportService.queryNode(reportId, xpathMd5, threadNum));
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
            moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_REPORT,
            needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ResponseResult<String> getExportDownLoadUrl(Long reportId) {
        return ResponseResult.success(reportService.downloadPDFPath(reportId));
    }

    @GetMapping("vlt/report/getLinkDiagram")
    @ApiOperation("LT版-业务活动链路图")
    public io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse getLtLinkDiagram(@Validated ReportLinkDiagramReq2 linkDiagramReq){
        return reportService.getLinkDiagram2(linkDiagramReq);
    }

    /**
     * 优化，如果接口响应耗时>5s,会引发指标呈现锯齿状，比如成功率看到的是：100% - 0% - 100% - 0
     * 若果某时刻tps、rt都是0；则更新成功率、并发数为上一个节点的值
     * @param reportTrendResp
     */
    private void formatReportTrendData(ReportTrendResp reportTrendResp) {
        List<String> timeList = reportTrendResp.getTime();
        if(CollectionUtils.isEmpty(timeList)) {
            return;
        }
        for(int i = 0; i < timeList.size(); i++) {
            if(i == 0) {
                continue;
            }
            if(checkDataIsZero(reportTrendResp.getTps().get(i)) && checkDataIsZero(reportTrendResp.getRt().get(i))) {
                reportTrendResp.getSuccessRate().set(i, reportTrendResp.getSuccessRate().get(i - 1));
                reportTrendResp.getConcurrent().set(i, reportTrendResp.getConcurrent().get(i - 1));
            }
        }
    }

    private Boolean checkDataIsZero(String data) {
        if(StringUtils.isBlank(data)) {
            return true;
        }
        return BigDecimal.ZERO.compareTo(new BigDecimal(data)) == 0;
    }
}