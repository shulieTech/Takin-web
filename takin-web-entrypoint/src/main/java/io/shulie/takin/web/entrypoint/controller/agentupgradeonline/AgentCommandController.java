package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentCommandRequest;
import io.shulie.takin.web.biz.service.agentcommand.IAgentCommandProcessor;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description agent指令接口
 * @Author ocean_wll
 * @Date 2021/11/11 11:01 上午
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "agent")
@Api(tags = "接口：agent指令接口")
@Slf4j
public class AgentCommandController {

    private final Map<String, IAgentCommandProcessor> commandProcessorMap = new HashMap<>();

    public AgentCommandController(List<IAgentCommandProcessor> processorList) {
        processorList.forEach(processor -> commandProcessorMap.put(processor.getCommand().getCommand(), processor));
    }

    @PostMapping("/command")
    public Object execCommand(@Validated @RequestBody AgentCommandRequest commandRequest) {
        IAgentCommandProcessor processor = commandProcessorMap.get(commandRequest.getCommandId());
        if (processor != null) {
            return processor.process(commandRequest);
        }
        return null;
    }

}
