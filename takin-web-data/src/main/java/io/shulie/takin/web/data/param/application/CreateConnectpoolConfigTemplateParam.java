package io.shulie.takin.web.data.param.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ConnectpoolConfigTemplateEntity;

/**
 * 连接池配置模版表(ConnectpoolConfigTemplate)创建入参类
 *
 * @author 南风
 * @date 2021-08-30 10:31:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateConnectpoolConfigTemplateParam extends ConnectpoolConfigTemplateEntity {

}
