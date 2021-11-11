package io.shulie.takin.web.biz.service.agentcommand;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentCommandRequest;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;

/**
 * @Description agent命令处理器
 * @Author ocean_wll
 * @Date 2021/11/11 2:44 下午
 */
public interface IAgentCommandProcessor {

    /**
     * 获取指令枚举
     *
     * @return AgentCommandEnum
     */
    AgentCommandEnum getCommand();

    /**
     * 处理agent命令
     *
     * @param commandRequest 命令请求
     * @return 每个命令处理器都需要返回一个响应
     */
    Object process(AgentCommandRequest commandRequest);
}
