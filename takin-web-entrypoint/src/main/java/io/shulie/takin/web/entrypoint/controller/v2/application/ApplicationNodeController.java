package io.shulie.takin.web.entrypoint.controller.v2.application;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.application.ApplicationNodeService;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeOperateProbeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationNodeQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeDashBoardResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationNodeResponse;
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
 * @author liuchuan
 * @date 2021/6/9 4:30 下午
 */
@RestController("v2.application.node")
@RequestMapping(APIUrls.TAKIN_API_URL + "v2/application/node/")
@Api(tags = "接口-v2: 应用实例-节点管理")
public class ApplicationNodeController {

    @Autowired
    private ApplicationNodeService applicationNodeService;

    @ApiOperation("|_ 节点列表")
    @GetMapping("list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ApplicationNodeResponse> getNodesByAppId(@Validated ApplicationNodeQueryRequest request) {
        return applicationNodeService.pageNodesV2(request);
    }

    @ApiOperation("|_ 节点信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "applicationId", value = "应用id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping("dashboard")
    public ApplicationNodeDashBoardResponse getNodeDashBoardByAppId(@RequestParam Long applicationId) {
        return applicationNodeService.getApplicationNodeInfo(applicationId);
    }

    @ApiOperation("|_ 探针操作")
    @PostMapping("probe/operate")
    public void operateProbe(@Validated @RequestBody ApplicationNodeOperateProbeRequest request) {
        applicationNodeService.operateProbe(request);
    }

}
