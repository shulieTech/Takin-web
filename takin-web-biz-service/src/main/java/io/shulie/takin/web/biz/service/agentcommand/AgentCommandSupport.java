package io.shulie.takin.web.biz.service.agentcommand;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentCommandRequest;

/**
 * @Description agent指令
 * @Author ocean_wll
 * @Date 2021/11/11 2:45 下午
 */
public abstract class AgentCommandSupport implements IAgentCommandProcessor {

    /**
     * 执行指令
     *
     * @param commandRequest 指令参数
     * @return Object
     */
    public abstract Object process0(AgentCommandRequest commandRequest);

    @Override
    public Object process(AgentCommandRequest commandRequest) {
        return process0(commandRequest);
    }

}
