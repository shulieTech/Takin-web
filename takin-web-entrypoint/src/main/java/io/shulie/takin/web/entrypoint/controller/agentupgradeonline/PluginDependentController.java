package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "")
public class PluginDependentController {

    @ApiOperation("")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping
    public void index(@RequestParam Long id) {

    }

}
