package io.shulie.takin.web.data.dao.agentupgradeonline;

import io.shulie.takin.web.data.result.application.AgentReportDetailResult;

import java.util.List;

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

}

