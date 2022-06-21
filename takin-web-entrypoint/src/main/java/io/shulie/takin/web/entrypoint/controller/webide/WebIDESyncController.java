package io.shulie.takin.web.entrypoint.controller.webide;

import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptListRequest;
import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;
import io.shulie.takin.web.biz.service.webide.WebIDESyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: 南风
 * @Date: 2022/6/17 4:20 下午
 */
@RequestMapping("/api/webide")
@Api(tags = "webIDE数据同步", value = "webIDE数据同步")
@RestController
public class WebIDESyncController {

    @Resource
    private WebIDESyncService webIDESyncService;

    @ApiOperation("同步业务流程并启动调试")
    @GetMapping("/script/businessFlow/sync")
    public void syncScriptBusinessFlow(@RequestBody WebIDESyncScriptRequest request){
        if(Objects.nonNull(request)){
            webIDESyncService.syncScript(request);
        }
    }
}
