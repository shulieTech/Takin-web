package io.shulie.takin.web.entrypoint.controller.application;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.web.common.annocation.AuthVerification;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeDashBoardQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeResponse;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mubai
 * @date 2020-09-23 19:51
 */

@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
@Api(tags = "ApplicationNodeController", value = "应用实例（节点）管理")
public class ApplicationNodeController {

    @Autowired
    private ApplicationNodeService applicationNodeService;

    @ApiOperation("根据应用id获取节点信息")
    @GetMapping("application/node/list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ApplicationNodeResponse> getNodesByAppId(ApplicationNodeQueryRequest request) {
        return applicationNodeService.pageNodes(request);
    }

    @ApiOperation("根据应用id获取节点汇总信息")
    @GetMapping("application/node/dashboard")
    public ApplicationNodeDashBoardResponse getNodeDashBoardByAppId(ApplicationNodeDashBoardQueryRequest request) {
        return applicationNodeService.getApplicationNodeAmount(request);
    }

    @ApiOperation("根据agentId获取节点信息")
    @GetMapping("application/node/info")
    public ApplicationNodeResponse getNodeByAgentId(@Validated @NotNull String agentId) {
        return applicationNodeService.getNodeByAgentId(agentId);
    }

    /**
     * 删除节点
     *
     * @return
     */
    @ApiOperation("删除节点")
    @GetMapping("application/node/info/deleteNode")
    public ResponseResult<String> deleteZkNode(@RequestParam(value = "appName") String appName,
        @RequestParam(value = "agentId", required = false) String agentId) {
        applicationNodeService.deleteZkNode(appName, agentId);
        return ResponseResult.success("删除成功");
    }

}
