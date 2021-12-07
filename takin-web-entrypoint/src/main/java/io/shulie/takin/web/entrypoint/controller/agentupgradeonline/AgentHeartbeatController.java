package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandResBO;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentHeartbeatRequest;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentHeartbeatService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description agent心跳接口
 * @Author ocean_wll
 * @Date 2021/11/11 11:01 上午
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "agent")
@Api(tags = "接口：agent心跳接口")
@Slf4j
public class AgentHeartbeatController {

    @Resource
    private AgentHeartbeatService agentHeartbeatService;

    @PostMapping("/heartbeat")
    public List<AgentCommandResBO> process(@Validated @RequestBody AgentHeartbeatRequest heartbeatRequest) {
        return agentHeartbeatService.process(heartbeatRequest);
    }
}
