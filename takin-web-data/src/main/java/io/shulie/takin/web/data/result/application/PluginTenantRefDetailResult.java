package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.PluginTenantRefEntity;

/**
 * 插件版本库(PluginTenantRef)详情出参类
 *
 * @author ocean_wll
 * @date 2021-11-10 17:54:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PluginTenantRefDetailResult extends PluginTenantRefEntity {

}
