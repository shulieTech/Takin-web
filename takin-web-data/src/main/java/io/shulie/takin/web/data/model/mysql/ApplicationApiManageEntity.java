package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_application_api_manage")
public class ApplicationApiManageEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 应用主键
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 应用名称
     */
    @TableField(value = "APPLICATION_NAME")
    private String applicationName;

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
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;

    /**
     * api
     */
    @TableField(value = "api")
    private String api;

    /**
     * 请求类型
     */
    @TableField(value = "method")
    private String method;
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;

    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
