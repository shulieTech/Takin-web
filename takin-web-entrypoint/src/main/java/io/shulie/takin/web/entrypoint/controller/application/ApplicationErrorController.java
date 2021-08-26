package io.shulie.takin.web.entrypoint.controller.application;

import java.util.List;

import io.shulie.takin.web.biz.service.application.ApplicationErrorService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationErrorQueryInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationErrorOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用错误信息
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
@Api(tags = "ApplicationErrorController", value = "应用错误信息")
public class ApplicationErrorController {

    @Autowired
    private ApplicationErrorService applicationErrorService;

    @GetMapping("application/error/list")
    @ApiOperation("应用异常列表")
    public List<ApplicationErrorOutput> list(ApplicationErrorQueryInput request) {
        return applicationErrorService.list(request);
    }
}
