package io.shulie.takin.web.data.param.agentupgradeonline;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.AgentReportEntity;

/**
 * 探针心跳数据(AgentReport)创建入参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:35:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateAgentReportParam extends AgentReportEntity {

}
