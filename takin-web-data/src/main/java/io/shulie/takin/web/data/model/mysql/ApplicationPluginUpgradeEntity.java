package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.NewBaseEntity;
import lombok.ToString;

/**
 * 应用升级单(ApplicationPluginUpgrade)实体类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_plugin_upgrade")
@ToString(callSuper = true)
public class ApplicationPluginUpgradeEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 507807328688718294L;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 升级批次 根据升级内容生成MD5
     */
    private String upgradeBatch;

    /**
     * 升级内容 格式 {pluginId,pluginId}
     */
    private String upgradeContext;

    /**
     * 升级单版本
     */
    private Integer upgradeVersion;

    /**
     * 下载地址
     */
    private String downloadPath;

    /**
     * 升级状态 0 未升级 1升级成功
     */
    private Integer pluginUpgradeStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

}
