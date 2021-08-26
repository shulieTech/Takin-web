package io.shulie.takin.web.entrypoint.controller.application;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.pojo.request.application.CompareApplicationMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.request.application.ListApplicationMiddlewareRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationMiddlewareCountResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationMiddlewareListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用中间件(ApplicationMiddleware)controller
 *
 * @author liuchuan
 * @date 2021-06-30 16:11:28
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "application/middleware/")
@Api(tags = "接口: 应用中间件")
public class ApplicationMiddlewareController {

    @Autowired
    private ApplicationMiddlewareService applicationMiddlewareService;

    @ApiOperation("|_ 列表")
    @GetMapping("list")
    public PagingList<ApplicationMiddlewareListResponse> index(
        @Validated ListApplicationMiddlewareRequest listApplicationMiddlewareRequest) {
        return applicationMiddlewareService.page(listApplicationMiddlewareRequest);
    }

    @ApiOperation("|_ 统计")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "applicationId", value = "应用id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping("count")
    public ApplicationMiddlewareCountResponse count(@RequestParam Long applicationId) {
        return applicationMiddlewareService.countSome(applicationId);
    }

    @ApiOperation("|_ 重新比对")
    @PostMapping("compare")
    public void compare(@Validated @RequestBody CompareApplicationMiddlewareRequest request) {
        applicationMiddlewareService.compare(request.getApplicationId());
    }

}
