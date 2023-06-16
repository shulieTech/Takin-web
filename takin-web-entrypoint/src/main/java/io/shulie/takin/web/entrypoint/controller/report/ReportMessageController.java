package io.shulie.takin.web.entrypoint.controller.report;

import com.pamirs.takin.common.enums.ResultCodeEnum;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportMessageStatusCodeDTO;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageCodeReq;
import io.shulie.takin.adapter.api.model.request.report.ReportMessageDetailReq;
import io.shulie.takin.web.biz.service.report.ReportMessageService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "请求报文")
public class ReportMessageController {

    @Resource
    private ReportMessageService reportMessageService;

    @GetMapping("vlt/report/message/code")
    @ApiOperation("LT版-请求报文-状态码列表")
    public Response<List<ReportMessageStatusCodeDTO>> getReportMessageStatusCodeList(ReportMessageCodeReq req) {
        return Response.success(reportMessageService.getStatusCodeList(req));
    }

    @GetMapping("vlt/report/message/detail")
    @ApiOperation("LT版-请求报文-请求明细")
    public Response<ReportMessageDetailDTO> getReportMessageDetail(ReportMessageDetailReq req) {
        return Response.success(reportMessageService.getOneTraceDetail(req));
    }
}
