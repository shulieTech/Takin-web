package io.shulie.takin.web.entrypoint.controller.agent;
import io.shulie.takin.web.biz.service.application.ApplicationMiddlewareService;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuchuan
 * @date 2021/6/7 9:25 上午
 */
@Slf4j
@RestController
@RequestMapping
@Api(tags = "接口: agent 应用相关")
public class AgentApplicationController {

    @Autowired
    private ApplicationMiddlewareService applicationMiddlewareService;

    //todo agent改造点
    @ApiOperation("|_ 上报应用中间件")
    @PostMapping(AgentUrls.AGENT_PUSH_APPLICATION_MIDDLEWARE)
    public void pushMiddlewareList(@Validated @RequestBody PushMiddlewareRequest pushMiddlewareRequest) {
        applicationMiddlewareService.pushMiddlewareList(pushMiddlewareRequest);
    }

}
