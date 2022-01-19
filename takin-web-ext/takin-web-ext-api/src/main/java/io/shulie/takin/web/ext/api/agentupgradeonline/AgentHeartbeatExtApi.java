package io.shulie.takin.web.ext.api.agentupgradeonline;

import java.util.List;

import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;

/**
 * @Description 心跳扩展接口
 * @Author ocean_wll
 * @Date 2021/11/25 10:14 上午
 */
public interface AgentHeartbeatExtApi extends ExtensionPoint {

    /**
     * 获取 IAgentCommandProcessor 实现类处理不同的心跳指令
     *
     * 由于 IAgentCommandProcessor 类在biz-service模块下所以在当前接口定义中只定义返回Object对象，使用时加下类型判断
     *
     * @return IAgentCommandProcessor子类集合
     */
    List<Object> getAgentCommandProcessor();
}
