package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.ApplicationPluginUpgradeCreateRequest;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用升级单(ApplicationPluginUpgrade)controller
 *
 * @author ocean_wll
 * @date 2021-11-09 20:29:05
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "plugin/upgrade")
@Api(tags = "")
public class ApplicationPluginUpgradeController {

    @Autowired
    private ApplicationPluginUpgradeService upgradeService;

    @ApiOperation("|_ 创建升级单")
    @PostMapping("/create")
    public Response release(@Validated @RequestBody ApplicationPluginUpgradeCreateRequest createRequest) {
        return upgradeService.pluginUpgrade(createRequest);
    }

}
