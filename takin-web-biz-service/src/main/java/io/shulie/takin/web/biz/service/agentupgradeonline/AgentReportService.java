package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.util.List;
import java.util.Map;

import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;

/**
 * 探针心跳数据(AgentReport)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:32:59
 */
public interface AgentReportService {

    List<AgentReportDetailResult> getList(List<Long> appIds);

    List<AgentReportDetailResult> getListByStatus(List<Integer> statusList);

    Map<Long, Integer> appId2Count();

    /**
     * 不存在插入，存在更新
     *
     * @param createAgentReportParam CreateAgentReportParam 对象
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
