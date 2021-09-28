package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 指标采集配置
 */
@Data
@TableName(value = "t_indicators_collect_config")
public class IndicatorsCollectConfigEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测引擎名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 关联字典表dictionary_id
     */
    @TableField(value = "type")
    private String type;

    /**
     * 采集周期
     */
    @TableField(value = "collect_time")
    private Integer collectTime;

    /**
     * 指标抓去插件
     */
    @TableField(value = "plugin_path")
    private String pluginPath;

    /**
     * 启动类名称
     */
    @TableField(value = "class_name")
    private String className;

    /**
     * jar名称
     */
    @TableField(value = "jar_name")
    private String jarName;

    /**
     * 0-可用 1-不可用
     */
    @TableField(value = "status")
    private Boolean status;

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
