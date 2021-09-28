package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_application_ds_manage")
public class ApplicationDsManageEntity {
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
     * 存储类型 0:数据库 1:缓存
     */
    @TableField(value = "DB_TYPE")
    private Integer dbType;

    /**
     * 方案类型 0:影子库 1:影子表 2:影子server
     */
    @TableField(value = "DS_TYPE")
    private Integer dsType;

    /**
     * 数据库url,影子表需填
     */
    @TableField(value = "URL")
    private String url;

    /**
     * xml配置
     */
    @TableField(value = "CONFIG")
    private String config;

    /**
     * 解析后配置
     */
    @TableField(value = "PARSE_CONFIG")
    private String parseConfig;

    /**
     * 状态 0:启用；1:禁用
     */
    @TableField(value = "STATUS")
    private Integer status;

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
    @TableLogic
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;

    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
