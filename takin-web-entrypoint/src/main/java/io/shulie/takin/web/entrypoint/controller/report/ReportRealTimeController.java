package io.shulie.takin.web.entrypoint.controller.report;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceQueryDTO;
import io.shulie.takin.web.biz.pojo.response.report.ReportLinkDetailResponse;
import io.shulie.takin.web.biz.pojo.response.report.RiskTraceDetailRequest;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author qianshui
 * @date 2020/8/17 下午8:18
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "接口: 实况报告")
public class ReportRealTimeController {

    @Autowired
    private ReportRealTimeService reportRealTimeService;

    @GetMapping("/report/realtime/link/list")
    @ApiOperation("实况链路列表")
    private Response getLinkList(ReportTraceQueryDTO queryDTO) {
        queryDTO.setQueryType(2);
        if (queryDTO.getReportId() != null) {
            PageInfo<ReportTraceDTO> reportLinkListByReportId = reportRealTimeService.getReportLinkListByReportId(queryDTO);
            return Response.success(reportLinkListByReportId);
        }
        if (queryDTO.getStartTime() != null) {
            PageInfo<ReportTraceDTO> reportLinkList = reportRealTimeService.getReportLinkList(queryDTO);
            return Response.success(reportLinkList);
        }

        return Response.success(new PageInfo<>());
    }

    @ApiOperation("|_ 链路详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "traceId", value = "traceId", required = true,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "id", value = "amdb 报告踪迹详情的主键id",
            dataType = "integer", paramType = "query", defaultValue = "0"),
    })
    @GetMapping("/report/link/detail")
    private ReportLinkDetailResponse getLinkDetail(@RequestParam String traceId,
        @RequestParam(defaultValue = "0") Integer id) {
        return reportRealTimeService.getLinkDetail(traceId, id);
    }

    @ApiOperation("获取sre的trace详情")
    @PostMapping("/report/trace/sre/detail")
    private ReportLinkDetailResponse getSreTraceDetail(@RequestBody RiskTraceDetailRequest request) {
        return reportRealTimeService.getSreRiskLinkDetail(request.getTraceId(), request.getId());
    }

}
