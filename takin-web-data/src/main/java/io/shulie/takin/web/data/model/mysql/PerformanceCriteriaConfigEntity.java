package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 性能标准配置
 */
@Data
@TableName(value = "t_performance_criteria_config")
public class PerformanceCriteriaConfigEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0-针对应用配置 1-全局配置
     */
    @TableField(value = "env")
    private String env;

    /**
     * 应用ID
     */
    @TableField(value = "app_id")
    private Long appId;

    /**
     * 应用名称
     */
    @TableField(value = "app_name")
    private String appName;

    /**
     * 标准类型1-小于、2-小于等于、3-等于、4-大于、5-大于等于
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 标准值
     */
    @TableField(value = "value")
    private String value;

    /**
     * 是否达标0-达标、1-不达标
     */
    @TableField(value = "standard")
    private Boolean standard;

    /**
     * 是否删除 0-未删除、1-已删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

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
