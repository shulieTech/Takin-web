package io.shulie.takin.web.entrypoint.controller;

import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.dto.config.WhiteListSwitchDTO;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.config.ConfigService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "控制台白名单配置")
@Slf4j
public class GlobalController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @GetMapping("/console/switch/whitelist")
    @ApiOperation(value = "查看全局白名单开关")
    public Response<WhiteListSwitchDTO> getWhiteListSwitch() {
        WhiteListSwitchDTO switchDTO = new WhiteListSwitchDTO();
        switchDTO.setConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
        switchDTO.setSwitchFlagFix(configService.getAllowListSwitch(WebPluginUtils.traceTenantCommonExt()));
        return Response.success(switchDTO);
    }

    @PutMapping("/console/switch/whitelist/open")
    @ApiOperation(value = "打开全局白名单开关")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.PRESSURE_WHITELIST_SWITCH,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITELIST_SWITCH_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_WHITELIST_SWITCH,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response openWhiteListSwitch() {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.OPEN);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ACTION, BizOpConstants.OpTypes.OPEN);

        configService.updateAllowListSwitch(WebPluginUtils.traceTenantCommonExt(), true);
        //todo Agent改造点
        agentConfigCacheManager.evictAllowListSwitch("","");
        return Response.success();
    }

    @PutMapping("/console/switch/whitelist/close")
    @ApiOperation(value = "关闭全局白名单开关")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.PRESSURE_WHITELIST_SWITCH,
        logMsgKey = BizOpConstants.Message.MESSAGE_WHITELIST_SWITCH_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_WHITELIST_SWITCH,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public Response closeWhiteListSwitch() {
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CLOSE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ACTION, BizOpConstants.OpTypes.CLOSE);
        configService.updateAllowListSwitch(WebPluginUtils.traceTenantCommonExt(), false);
        //todo Agent改造点
        agentConfigCacheManager.evictAllowListSwitch("","");
        return Response.success();
    }

}
