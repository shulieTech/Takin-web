package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;

/**
 * @author TODO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_api_manage")
public class ApplicationApiManageEntity extends UserBaseEntity {
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

    @TableField(value = "is_agent_registe")
    private Integer isAgentRegiste;


    @TableField(value = "dept_id",fill = FieldFill.INSERT)
    private Long deptId;
}
