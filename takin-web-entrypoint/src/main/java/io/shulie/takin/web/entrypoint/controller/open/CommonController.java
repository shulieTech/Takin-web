package io.shulie.takin.web.entrypoint.controller.open;

import io.shulie.takin.web.biz.response.AnnualReportDetailResponse;
import io.shulie.takin.web.biz.service.AnnualReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/5/14 下午7:34
 */
@RestController
@RequestMapping("/open/")
@Api(tags = "接口: 公共API")
public class CommonController {

    @Autowired
    private AnnualReportService annualReportService;

    @ApiOperation("|_ 年度总结")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "tenantId", value = "租户id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping("annualReport")
    public AnnualReportDetailResponse getOperate(@RequestParam Long tenantId) {
        return annualReportService.getAnnualReportByTenantId(tenantId);
    }

}
