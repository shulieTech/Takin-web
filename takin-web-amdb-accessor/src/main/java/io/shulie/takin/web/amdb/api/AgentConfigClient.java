package io.shulie.takin.web.amdb.api;

import io.shulie.amdb.common.dto.agent.AgentConfigDTO;
import io.shulie.amdb.common.dto.agent.AgentStatInfoDTO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.query.fastagentaccess.AgentConfigQueryDTO;



/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 3:55 下午
 */
public interface AgentConfigClient {

    /**
     * agent配置状态统计
     *
     * @param configKey   配置key
     * @param projectName 应用名
     * @return AgentStatInfoDTO
     */
    AgentStatInfoDTO agentConfigStatusCount(String configKey, String projectName);

    /**
     * 配置生效列表查询
     *
     * @param queryDTO AgentConfigQueryDTO对象
     * @return PagingList<AgentConfigDTO>
     */
    PagingList<AgentConfigDTO> effectList(AgentConfigQueryDTO queryDTO);
}
