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
}
