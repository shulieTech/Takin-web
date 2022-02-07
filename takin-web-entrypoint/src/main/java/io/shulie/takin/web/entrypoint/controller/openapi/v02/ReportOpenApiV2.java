package io.shulie.takin.web.entrypoint.controller.openapi.v02;

import javax.annotation.Resource;

import io.shulie.takin.cloud.sdk.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author caijianying
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_OPEN_API_V02_URL+"report/")
@Api(tags = "场景报告模块")
public class ReportOpenApiV2 {
    @Resource
    private ReportService reportService;

    @GetMapping("summary/list")
    public ResponseResult getSummaryResult(Long reportId){
        final NodeTreeSummaryResp querySummaryList = reportService.querySummaryList(reportId);
        return ResponseResult.success(querySummaryList);
    }



}
