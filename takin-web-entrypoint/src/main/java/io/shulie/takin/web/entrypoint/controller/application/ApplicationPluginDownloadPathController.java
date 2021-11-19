package io.shulie.takin.web.entrypoint.controller.application;

import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathUpdateInput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationPluginPathDetailResponse;
import io.shulie.takin.web.biz.service.application.ApplicationAgentPathTypeService;
import io.shulie.takin.web.common.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 探针根目录(ApplicationPluginDownloadPath)controller
 *
 * @author 南风
 * @date 2021-11-10 16:11:35
 */
@RestController
@RequestMapping(APIUrls.TRO_API_URL + "plugin/path")
@Api(tags = "探针存放根目录", value = "探针根目录管理")
public class ApplicationPluginDownloadPathController {

    @Autowired
    private ApplicationAgentPathTypeService pathTypeService;


    @ApiOperation("获取探针存放根目录配置")
    @GetMapping("/config")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response<ApplicationPluginPathDetailResponse> queryConfigDetail() {
        return  pathTypeService.queryConfigDetail();
    }


    @ApiOperation("创建探针存放根目录配置")
    @PutMapping("/config/create")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.CREATE
    )
    public Response createConfig(@Validated @RequestBody ApplicationPluginDownloadPathInput createInput) {
        return  pathTypeService.createConfig(createInput);
    }

    @ApiOperation("更新探针存放根目录配置")
    @PutMapping("/config/update")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    public Response updateConfig(@Validated @RequestBody ApplicationPluginDownloadPathUpdateInput updateInput) {
        return  pathTypeService.updateConfig(updateInput);
    }

    @ApiOperation("检查探针存放根目录配置有效性")
    @GetMapping("/config/effectiveness")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public Response queryConfigEffectiveness() {
        return pathTypeService.validEfficient();
    }
}
