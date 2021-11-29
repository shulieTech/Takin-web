package io.shulie.takin.web.biz.service.agentcommand;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandReqBO;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandResBO;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
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
     * @param agentHeartbeatBO agent心跳数据
     * @param commandParam     命令参数
     */
    void process(AgentHeartbeatBO agentHeartbeatBO, AgentCommandReqBO commandParam);

    /**
     * 处理心跳数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @return AgentCommandBO对象
     */
    AgentCommandResBO dealHeartbeat(AgentHeartbeatBO agentHeartbeatBO);
}
