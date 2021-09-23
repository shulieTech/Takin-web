package io.shulie.takin.web.data.param.fastagentaccess;

import io.shulie.takin.web.data.model.mysql.AgentVersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent版本管理(AgentVersion)更新入参类
 *
 * @author liuchuan
 * @date 2021-08-11 19:44:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateAgentVersionParam extends AgentVersionEntity {

}
