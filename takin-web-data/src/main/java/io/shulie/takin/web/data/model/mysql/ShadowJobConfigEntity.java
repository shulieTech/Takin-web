package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 影子JOB任务配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_shadow_job_config")
public class ShadowJobConfigEntity extends BaseEntity {

    /**
     * 应用ID
     */
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * 任务名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * JOB类型 0-quartz、1-elastic-job、2-xxl-job
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 配置代码
     */
    @TableField(value = "config_code")
    private String configCode;

    /**
     * 0-可用 1-不可用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 0-可用 1-不可用
     */
    @TableField(value = "active")
    private Integer active;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}
