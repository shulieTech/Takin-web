package io.shulie.takin.web.entrypoint.controller.leakcheck;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@RestController
@RequestMapping("/api/leak/report")
@Api(tags = "验证结果管理")
public class LeakCheckResultController {

    @Autowired
    private VerifyTaskReportService reportService;

    @GetMapping("/detail")
    @ApiOperation("验证结果详情")
    public LeakVerifyTaskResultResponse report(@NotNull Long reportId) {
        LeakVerifyTaskReportQueryRequest queryRequest = new LeakVerifyTaskReportQueryRequest();
        queryRequest.setReportId(reportId);
        return reportService.getVerifyTaskReport(queryRequest);
    }
}
