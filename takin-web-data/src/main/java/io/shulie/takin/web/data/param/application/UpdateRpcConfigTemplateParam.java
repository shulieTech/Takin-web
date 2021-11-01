package io.shulie.takin.web.data.param.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.RpcConfigTemplateEntity;

/**
 * rpc框架配置模版(RpcConfigTemplate)更新入参类
 *
 * @author 南风
 * @date 2021-08-25 15:00:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateRpcConfigTemplateParam extends RpcConfigTemplateEntity {

}
