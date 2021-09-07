package io.shulie.takin.web.entrypoint.controller.fastagentaccess;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
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
 * agent配置管理(AgentConfig)controller
 *
 * @author ocean_wll
 * @date 2021-08-12 18:54:54
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL + "fast/agent/access/config")
@Api(tags = "接口：agent配置")
public class AgentConfigController {

    @Autowired
    private AgentConfigService agentConfigService;

    @Autowired
    private AmdbManageService amdbManageService;

    @ApiOperation("|_ 获取所有配置项")
    @GetMapping("/allKey")
    public List<Map<String, String>> allConfigKey() {
        return agentConfigService.getAllGlobalKey();
    }

    @ApiOperation("|_ 获取所有应用名列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "keyword", value = "关键词")
    })
    @GetMapping("/allApplication")
    public List<String> allApplication(@RequestParam(required = false) String keyword) {
        return agentConfigService.getAllApplication(keyword);
    }

    @ApiOperation("|_ 检查中文key是否重复，true为重复，false不重复")
    @GetMapping("/checkZhKey")
    public WebResponse checkZhKey(@RequestParam String zhKey) {
        return WebResponse.success(agentConfigService.checkZhKey(zhKey));
    }

    @ApiOperation("|_ 检查英文key是否重复，true为重复，false不重复")
    @GetMapping("/checkEnKey")
    public WebResponse checkEnKey(@RequestParam String enKey) {
        return WebResponse.success(agentConfigService.checkEnKey(enKey));
    }

    @ApiOperation("|_ 单选获取可选值列表")
    @GetMapping("/getValueOption")
    public List<String> getValueOption(@RequestParam Long id) {
        return agentConfigService.getValueOption(id);
    }

    @ApiOperation("|_ 列表查询(不分页)")
    @GetMapping("/list")
    public List<AgentConfigListResponse> agentConfigList(AgentConfigQueryRequest queryRequest) {
        return agentConfigService.list(queryRequest);
    }

    @ApiOperation("|_ 编辑（同时传id和projectName则认为是将全局配置改成应用配置）")
    @PutMapping("/update")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.AGENT_CONFIG,
        needAuth = ActionTypeEnum.UPDATE
    )
    public WebResponse update(@RequestBody AgentConfigUpdateRequest updateRequest) {
        agentConfigService.update(updateRequest);
        return WebResponse.success();
    }

    @ApiOperation("|_ 使用全局配置")
    @DeleteMapping("/useGlobal")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.AGENT_CONFIG,
        needAuth = ActionTypeEnum.DELETE
    )
    public WebResponse useGlobal(@RequestParam Long id) {
        agentConfigService.useGlobal(id);
        return WebResponse.success();
    }

    @ApiOperation("|_ agent使用：配置参数查询")
    @PostMapping("/agentConfig")
    public Map<String, String> agentConfig(@RequestBody AgentDynamicConfigQueryRequest queryRequest) {
        return agentConfigService.agentConfig(queryRequest);
    }

    @ApiOperation("|_ 插件状态，根据agentId返回所有数据，筛选在前端实现")
    @GetMapping("/pluginList")
    public List<PluginLoadListResponse> pluginList(String agentId) {
        return amdbManageService.pluginList(agentId);
    }

    @ApiOperation("|_ 配置概况查询")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectName", value = "应用名"),
        @ApiImplicitParam(name = "enKey", value = "配置英文名", required = true)
    })
    @GetMapping("/countStatus")
    public AgentConfigStatusCountResponse countStatus(@RequestParam String enKey,
        @RequestParam(required = false) String projectName) {
        return amdbManageService.agentConfigStatusCount(enKey, projectName);
    }

    @ApiOperation("|_ 配置生效异常列表查询")
    @GetMapping("/effect/list")
    public PagingList<AgentConfigEffectListResponse> agentConfigEffectList(@Validated
        AgentConfigEffectQueryRequest queryRequest) {
        return agentConfigService.queryConfigEffectList(queryRequest);
    }



}
