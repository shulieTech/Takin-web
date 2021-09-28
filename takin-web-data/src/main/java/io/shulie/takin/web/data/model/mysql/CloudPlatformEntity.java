package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_cloud_platform")
public class CloudPlatformEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 云平台名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 插件类全路径
     */
    @TableField(value = "class_path")
    private String classPath;

    /**
     * 平台授权参数
     */
    @TableField(value = "authorize_param")
    private String authorizeParam;

    /**
     * Jar包名称
     */
    @TableField(value = "jar_name")
    private String jarName;

    /**
     * 状态 0:启用  1： 冻结
     */
    @TableField(value = "status")
    private Boolean status;

    /**
     * 状态 0: 正常 1： 删除
     */
    @TableField(value = "is_delete")
    private Boolean isDelete;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_update")
    private LocalDateTime gmtUpdate;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
