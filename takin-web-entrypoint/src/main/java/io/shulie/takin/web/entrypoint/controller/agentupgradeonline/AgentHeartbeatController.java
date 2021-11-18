package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandBO;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentHeartbeatRequest;
import io.shulie.takin.web.biz.service.agentcommand.IAgentCommandProcessor;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentHeartbeatService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
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
    private ThreadPoolExecutor agentHeartbeatThreadPool;

    @Resource
    private AgentHeartbeatService agentHeartbeatService;

    private final Map<Long, IAgentCommandProcessor> commandProcessorMap = new HashMap<>();

    public AgentHeartbeatController(List<IAgentCommandProcessor> processorList) {
        processorList.forEach(processor -> commandProcessorMap.put(processor.getCommand().getCommand(), processor));
    }

    @PostMapping("/heartbeat")
    public List<AgentCommandBO> process(@Validated @RequestBody AgentHeartbeatRequest heartbeatRequest) {

        // 先处理心跳数据，因为心跳数据里可能会主动抛一些业务异常
        List<AgentCommandBO> commandBOList = agentHeartbeatService.process(heartbeatRequest);

        // 异步处理上报的命令数据
        if (!CollectionUtils.isEmpty(heartbeatRequest.getCommandResult())) {
            heartbeatRequest.getCommandResult().forEach(commandResult ->
                agentHeartbeatThreadPool.execute(() -> {
                    IAgentCommandProcessor processor = commandProcessorMap.get(commandResult.getId());
                    if (processor != null) {
                        processor.process(commandResult);
                    }
                })
            );
        }

        return commandBOList;
    }
}
