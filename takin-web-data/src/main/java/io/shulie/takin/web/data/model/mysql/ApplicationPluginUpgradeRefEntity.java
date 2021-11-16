package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)实体类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:41:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_plugin_upgrade_ref")
@ToString(callSuper = true)
public class ApplicationPluginUpgradeRefEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 261194342901298625L;

    /**
     * 升级批次
     */
    private String upgradeBatch;

    /**
     * 插件id
     */
    private Long pluginId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 环境标识
     */
    private String envCode;

    /**
     * 租户Id
     */
    private Long tenantId;

}
