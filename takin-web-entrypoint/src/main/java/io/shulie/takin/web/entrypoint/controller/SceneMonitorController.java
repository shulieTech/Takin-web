package io.shulie.takin.web.entrypoint.controller;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.scene.SceneMonitorListResponse;
import io.shulie.takin.web.biz.service.scene.SceneMonitorService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方登录服务表(SceneMonitor)controller
 *
 * @author liuchuan
 * @date 2021-12-29 10:21:25
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "sceneMonitor/")
@Api(tags = "接口: 场景监控")
public class SceneMonitorController {

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @ApiOperation("|_ 场景下的监控列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "sceneId", value = "压测场景id", required = true,
            dataType = "long", paramType = "query")
    })
    @GetMapping("list")
    public List<SceneMonitorListResponse> index(@RequestParam Long sceneId) {
        return sceneMonitorService.listBySceneId(sceneId);
    }

}
