package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_scene_business_activity_ref")
public class SceneBusinessActivityRefEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 业务活动id
     */
    @TableField(value = "business_activity_id")
    private Long businessActivityId;

    /**
     * 业务活动名称
     */
    @TableField(value = "business_activity_name")
    private String businessActivityName;

    /**
     * 关联应用id，多个用,隔开
     */
    @TableField(value = "application_ids")
    private String applicationIds;

    /**
     * 目标值，json
     */
    @TableField(value = "goal_value")
    private String goalValue;

    /**
     * 绑定关系：jmx文件采样器名称
     */
    @TableField(value = "bind_ref")
    private String bindRef;

    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "create_name")
    private String createName;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "update_name")
    private String updateName;

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
}
