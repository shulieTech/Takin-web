package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 挡板实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_link_guard")
public class LinkGuardEntity extends BaseEntity {

    /**
     * 项目名称
     */
    @TableField(value = "application_name")
    private String applicationName;

    /**
     * 应用id
     */
    @TableField(value = "application_id")
    private Long applicationId;

    /**
     * 出口信息
     */
    @TableField(value = "method_info")
    private String methodInfo;

    /**
     * GROOVY脚本
     */
    @TableField(value = "groovy")
    private String groovy;

    /**
     * 租户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 用户id
     */
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 0:未启用；1:启用
     */
    @TableField(value = "is_enable")
    private Integer isEnable;


}
