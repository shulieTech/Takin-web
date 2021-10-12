package io.shulie.takin.web.entrypoint.controller.openapi;

import java.util.List;

import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 无涯
 * @date 2021/3/25 10:54 上午
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_OPEN_API_URL + "/application")
public class ApplicationOpenApi {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/center/list")
    @ApiOperation("应用列表查询接口")
    public List<ApplicationListResponse> getApplications(@ApiParam(name = "appNames", value = "应用名称,支持批量，逗号隔开")
        String appNames) {
        return applicationService.getApplicationList(appNames);
    }
}
