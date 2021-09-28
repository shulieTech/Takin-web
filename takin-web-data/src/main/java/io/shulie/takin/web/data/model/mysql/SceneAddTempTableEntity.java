package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 场景添加暂存表
 */
@Data
@TableName(value = "t_scene_add_temp_table")
public class SceneAddTempTableEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 业务链路id
     */
    @TableField(value = "BUSENESS_ID")
    private String busenessId;

    /**
     * 技术链路id
     */
    @TableField(value = "TECH_ID")
    private String techId;

    /**
     * 业务链路的上级业务链路
     */
    @TableField(value = "PARENT_BUSINESS_ID")
    private String parentBusinessId;

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
