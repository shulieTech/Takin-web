package io.shulie.takin.web.entrypoint.controller.simplify;

import com.pamirs.takin.entity.domain.query.agent.AppMiddlewareQuery;
import io.shulie.takin.web.biz.service.simplify.AppMiddlewareInfoService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-03-25 17:42
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "应用中间件信息", value = "应用中间件信息")
public class AppMiddlewareInfoController {

    @Autowired
    private AppMiddlewareInfoService appMiddlewareInfoService;

    @ApiOperation("应用中间件查询")
    @GetMapping(value = "app/middleware/query")
    public Response queryPage(@RequestParam("pageSize") Integer pageSize,
        @RequestParam("pageNum") Integer pageNum,
        @RequestParam("applicationId") Long applicationId) {
        try {
            AppMiddlewareQuery query = new AppMiddlewareQuery();
            query.setPageNum(pageNum);
            query.setPageSize(pageSize);
            query.setApplicationId(applicationId);
            return appMiddlewareInfoService.queryPage(query);
        } catch (Exception e) {
            return Response.fail(e.getMessage());
        }
    }
}
