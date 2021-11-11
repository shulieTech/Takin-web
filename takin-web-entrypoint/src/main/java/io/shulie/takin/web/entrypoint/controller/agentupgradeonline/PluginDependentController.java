package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
import io.shulie.takin.web.biz.service.agentupgradeonline.PluginDependentService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件依赖库(PluginDependent)controller
 *
 * @author ocean_wll
 * @date 2021-11-09 20:25:30
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "pluginDependent")
@Api(tags = "接口：插件依赖管理")
@Validated
public class PluginDependentController {

    @Resource
    private PluginDependentService pluginDependentService;

    @ApiOperation("|_ 查询插件依赖")
    @GetMapping("/query")
    public List<PluginInfo> queryDependent(@NotBlank(message = "插件名不能为空") @RequestParam("pluginName") String pluginName,
        @NotBlank(message = "插件版本不能为空") @RequestParam("pluginVersion") String pluginVersion) {
        return pluginDependentService.queryDependent(pluginName, pluginVersion);
    }

}
