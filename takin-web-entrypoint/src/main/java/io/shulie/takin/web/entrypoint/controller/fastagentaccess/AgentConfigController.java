package io.shulie.takin.web.entrypoint.controller.fastagentaccess;

import java.util.List;
import java.util.Map;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigEffectQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentDynamicConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigEffectListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigStatusCountResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.PluginLoadListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.biz.service.fastagentaccess.AmdbManageService;
import io.shulie.takin.web.common.annocation.Trimmed;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.domain.WebResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * agent????????????(AgentConfig)controller
 *
 * @author ocean_wll
 * @date 2021-08-12 18:54:54
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "fast/agent/access/config")
@Api(tags = "?????????agent??????")
public class AgentConfigController {

    @Autowired
    private AgentConfigService agentConfigService;

    @Autowired
    private AmdbManageService amdbManageService;

    @ApiOperation("|_ ?????????????????????")
    @GetMapping("/allKey")
    public List<Map<String, String>> allConfigKey() {
        return agentConfigService.getAllGlobalKey();
    }

    @ApiOperation("|_ ???????????????????????????")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "keyword", value = "?????????")
    })
    @GetMapping("/allApplication")
    @AuthVerification(
        moduleCode = ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<String> allApplication(@RequestParam(required = false) @Trimmed(value = Trimmed.TrimmerType.SIMPLE) String keyword) {
        return agentConfigService.getAllApplication(keyword);
    }

    @ApiOperation("|_ ????????????key???????????????true????????????false?????????")
    @GetMapping("/checkZhKey")
    public WebResponse checkZhKey(@RequestParam String zhKey) {
        return WebResponse.success(agentConfigService.checkZhKey(zhKey));
    }

    @ApiOperation("|_ ????????????key???????????????true????????????false?????????")
    @GetMapping("/checkEnKey")
    public WebResponse checkEnKey(@RequestParam String enKey) {
        return WebResponse.success(agentConfigService.checkEnKey(enKey));
    }

    @ApiOperation("|_ ???????????????????????????")
    @GetMapping("/getValueOption")
    public List<String> getValueOption(@RequestParam Long id) {
        return agentConfigService.getValueOption(id);
    }

    @ApiOperation("|_ ????????????(?????????)")
    @GetMapping("/list")
    public List<AgentConfigListResponse> agentConfigList(AgentConfigQueryRequest queryRequest) {
        return agentConfigService.list(queryRequest);
    }

    @ApiOperation("|_ ??????????????????id???projectName????????????????????????????????????????????????")
    @PutMapping("/update")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.AGENT_CONFIG,
        needAuth = ActionTypeEnum.UPDATE
    )
    public void update(@RequestBody AgentConfigUpdateRequest updateRequest) {
        agentConfigService.update(updateRequest);
    }

    @ApiOperation("|_ ??????????????????")
    @DeleteMapping("/useGlobal")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.AGENT_CONFIG,
        needAuth = ActionTypeEnum.DELETE
    )
    public WebResponse useGlobal(@RequestParam Long id) {
        agentConfigService.useGlobal(id);
        return WebResponse.success();
    }

    @ApiOperation("|_ agent???????????????????????????")
    @PostMapping("/agentConfig")
    public Map<String, String> agentConfig(@RequestBody AgentDynamicConfigQueryRequest queryRequest) {
        return agentConfigService.agentConfig(queryRequest);
    }

    @ApiOperation("|_ ?????????????????????agentId??????????????????????????????????????????")
    @GetMapping("/pluginList")
    public List<PluginLoadListResponse> pluginList(String agentId) {
        return amdbManageService.pluginList(agentId);
    }

    @ApiOperation("|_ ??????????????????")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectName", value = "?????????"),
        @ApiImplicitParam(name = "enKey", value = "???????????????", required = true)
    })
    @GetMapping("/countStatus")
    public AgentConfigStatusCountResponse countStatus(@RequestParam String enKey,
        @RequestParam(required = false) String projectName) {
        return amdbManageService.agentConfigStatusCount(enKey, projectName);
    }

    @ApiOperation("|_ ??????????????????????????????")
    @GetMapping("/effect/list")
    public PagingList<AgentConfigEffectListResponse> agentConfigEffectList(@Validated
        AgentConfigEffectQueryRequest queryRequest) {
        return agentConfigService.queryConfigEffectList(queryRequest);
    }

}