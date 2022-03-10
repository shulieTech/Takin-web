package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_script_manage_deploy")
@EnableSign
public class ScriptManageDeployEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "script_id")
    private Long scriptId;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 关联类型(业务活动)
     */
    @TableField(value = "ref_type")
    private String refType;

    /**
     * 关联值(活动id)
     */
    @TableField(value = "ref_value")
    private String refValue;

    /**
     * 脚本类型;0为jmeter脚本
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 0代表新建，1代表调试通过
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 操作人id
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 操作人名称
     */
    @TableField(value = "create_user_name")
    private String createUserName;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    @TableField(value = "script_version")
    private Integer scriptVersion;

    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 拓展字段
     */
    @TableField(value = "feature")
    private String feature;

    /**
     * 描述字段
     */
    @TableField(value = "description")
    private String description;

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;
}
