package io.shulie.takin.web.data.dao.agent;

import java.util.List;

import io.shulie.takin.web.data.result.agent.AgentPluginResult;

/**
 * @author fanxx
 * @date 2020/10/13 10:59 上午
 */
public interface AgentPluginDAO {

    List<AgentPluginResult> getAgentPluginList();

}
