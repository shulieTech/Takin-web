package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_scene_sla_ref")
public class SceneSlaRefEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 规则名称
     */
    @TableField(value = "sla_name")
    private String slaName;

    /**
     * 业务活动ID，多个用,隔开，-1表示所有
     */
    @TableField(value = "business_activity_ids")
    private String businessActivityIds;

    /**
     * 指标类型：0-RT 1-TPS 2-成功率 3-SA
     */
    @TableField(value = "target_type")
    private Integer targetType;

    /**
     * 警告/终止逻辑：json格式
     */
    @TableField(value = "condition")
    private String condition;

    /**
     * 状态：0-启用 1-禁用
     */
    @TableField(value = "status")
    private Integer status;

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
}
