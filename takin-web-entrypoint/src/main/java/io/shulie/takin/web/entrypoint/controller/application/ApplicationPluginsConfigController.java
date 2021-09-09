package io.shulie.takin.web.entrypoint.controller.application;

import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.ApplicationPluginsConfigService;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginsConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * (ApplicationPluginsConfig)表控制层
 *
 * @author caijy
 * @since 2021-05-18 17:22:49
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
@Api(tags = "应用管理-插件管理接口")
public class ApplicationPluginsConfigController {

    private final String apiPrefix = "application/plugins/config";

    @Autowired
    ApplicationPluginsConfigService configService;

    @ApiOperation("获取插件管理列表")
    @GetMapping(apiPrefix + "/page")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ApplicationPluginsConfigVO> findPage(ApplicationPluginsConfigParam param) {
        return configService.getPageByParam(param);
    }

    @ApiOperation("编辑插件")
    @PutMapping(apiPrefix + "/update")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.PLUGINS_MANAGER,
        logMsgKey = BizOpConstants.Message.MESSAGE_PLUGIN_MANAGER_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Boolean update(@RequestBody ApplicationPluginsConfigParam param) {
        return configService.update(param);
    }

    /*
     * 此操作放在程序初始化做
     * @see io.shulie.takin.web.biz.init.Initializer
     */
    //    @PostMapping(apiPrefix + "/init")
    //    @ApiOperation("应用管理初始化接口")
    //    public Response<Object> init() {
    //        configService.init();
    //        return Response.success(true);
    //    }

}
