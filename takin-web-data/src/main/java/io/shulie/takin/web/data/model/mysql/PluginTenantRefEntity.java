package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 插件版本库(PluginTenantRef)实体类
 *
 * @author ocean_wll
 * @date 2021-11-10 17:54:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_plugin_tenant_ref")
@ToString(callSuper = true)
public class PluginTenantRefEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -81964620322886913L;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件版本
     */
    private String pluginVersion;

    /**
     * 所属租户id
     */
    private Long owningTenantId;

    /**
     * 租户名称
     */
    private String owningTenantName;

}
