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

}

