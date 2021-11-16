package io.shulie.takin.web.biz.service.agentupgradeonline;

import io.shulie.takin.web.data.result.application.AgentReportDetailResult;

import java.util.List;
import java.util.Map;

/**
 * 探针心跳数据(AgentReport)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:32:59
 */
public interface AgentReportService {

    List<AgentReportDetailResult> getList(List<Long> appIds);

    List<AgentReportDetailResult> getListByStatus(List<Integer> statusList);

    Map<Long,Integer>  appId2Count();
}
