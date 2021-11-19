package io.shulie.takin.web.data.dao.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;

/**
 * 探针心跳数据(AgentReport)表数据库 dao 层
 *
 * @author ocean_wll
 * @date 2021-11-09 20:35:32
 */
public interface AgentReportDAO {

    List<AgentReportDetailResult> getList(List<Long> appIds);

    List<AgentReportDetailResult> getListByStatus(List<Integer> statusList);

    List<AgentReportDetailResult> getList();

    /**
     * 不存在则更新，存在则插入
     *
     * @param createAgentReportParam CreateAgentReportParam对象
     * @return 影响记录数
     */
    Integer insertOrUpdate(CreateAgentReportParam createAgentReportParam);

    /**
     * 清理过期的心跳数据
     */
    void clearExpiredData();

    /**
     * 根据ApplicationId和agentId查询心跳数据
     *
     * @param applicationId 应用Id
     * @param agentId       agentId
     * @return AgentReportDetailResult对象
     */
    AgentReportDetailResult queryAgentReportDetail(Long applicationId, String agentId);

    /**
     * 更新升级单对应的agentId
     *
     * @param id      当前升级单id
     * @param agentId agentId
     */
    void updateAgentIdById(Long id, String agentId);

}

