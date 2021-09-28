package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (ApplicationPluginsConfig)实体类
 *
 * @author liuchuan
 * @date 2021-05-18 16:48:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_plugins_config")
public class ApplicationPluginsConfigEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -20702119464891307L;

    private Long id;
    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 配置项
     */
    private String configItem;

    /**
     * 配置项key
     */
    private String configKey;

    /**
     * 配置说明
     */
    private String configDesc;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 用户Id
     */
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 租户ID
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifieTime;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 修改人ID
     */
    private Long modifierId;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;

    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
