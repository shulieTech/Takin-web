package io.shulie.takin.web.entrypoint.controller.fastagentaccess;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentDiscoverRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentListQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentStatusStatResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AmdbManageService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentDiscoverStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 探针管理
 *
 * @author ocean_wll
 * @date 2021/8/19 3:22 下午
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "fast/agent/access/probe")
@Api(tags = "接口：探针列表")
public class AgentListController {

    @Autowired
    private AmdbManageService amdbManageService;

    @ApiOperation("|_ 探针概况接口")
    @GetMapping("/overview")
    @AuthVerification(
        moduleCode = ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public AgentStatusStatResponse overview() {
        return amdbManageService.agentCountStatus();
    }

    @ApiOperation("|_ 列表分页查询")
    @GetMapping("/list")
    @AuthVerification(
        moduleCode = ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<AgentListResponse> agentList(AgentListQueryRequest queryRequest) {
        return amdbManageService.agentList(queryRequest);
    }

    @ApiOperation("|_ 新应用检测 0:ing,1:success,2:fail")
    @GetMapping("/discover")
    public WebResponse discover(@Validated AgentDiscoverRequest agentDiscoverRequest) {
        AgentDiscoverStatusEnum statusEnum = amdbManageService.agentDiscover(agentDiscoverRequest);
        return WebResponse.success(statusEnum.getVal());
    }

}
