package io.shulie.takin.web.data.dao.agent;

import java.util.List;

import io.shulie.takin.web.data.result.agent.AgentPluginLibSupportResult;

/**
 * @author fanxx
 * @date 2020/10/13 11:00 上午
 */
public interface AgentPluginLibSupportDAO {
    List<AgentPluginLibSupportResult> getAgentPluginLibSupportList();
}
