package io.shulie.takin.web.data.result.application;

import io.shulie.takin.web.data.model.mysql.AgentConfigEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent配置管理(AgentConfig)详情出参类
 *
 * @author ocean_wll
 * @date 2021-08-12 18:57:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AgentConfigDetailResult extends AgentConfigEntity {

}
