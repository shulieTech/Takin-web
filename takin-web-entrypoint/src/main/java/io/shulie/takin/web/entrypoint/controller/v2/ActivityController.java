package io.shulie.takin.web.entrypoint.controller.v2;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.activity.ListApplicationRequest;
import io.shulie.takin.web.biz.pojo.response.activity.BusinessApplicationListResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuchuan
 * @date 2022/2/17 5:08 下午
 */
@RequestMapping("/api/v2/activities")
@RestController("api.v2.activity")
@Api(tags = "接口: 业务活动")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @ApiOperation("|_ 业务流程下的应用列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "业务流程ids", value = "businessFlowIds", required = true,
            dataType = "array", paramType = "query")
    })
    @GetMapping("/application/list")
    public PagingList<BusinessApplicationListResponse> getActivityById(
        @Validated ListApplicationRequest listApplicationRequest) {
        return activityService.listApplicationByBusinessFlowIds(listApplicationRequest);
    }

}
