package io.shulie.takin.web.data.result.fastagentaccess;

import io.shulie.takin.web.data.model.mysql.AgentVersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent版本管理(AgentVersion)列表出参类
 *
 * @author liuchuan
 * @date 2021-08-11 19:44:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AgentVersionListResult extends AgentVersionEntity {

}
