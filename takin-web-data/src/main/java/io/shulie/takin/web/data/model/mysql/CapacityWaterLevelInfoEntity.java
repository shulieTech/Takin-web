package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测报告容量水位
 */
@Data
@TableName(value = "t_capacity_water_level_info")
public class CapacityWaterLevelInfoEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测报告整体概况ID
     */
    @TableField(value = "whole_report_id")
    private Long wholeReportId;

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
     * 主机总量
     */
    @TableField(value = "total_count")
    private String totalCount;

    /**
     * 达标主机
     */
    @TableField(value = "not_standard_count")
    private String notStandardCount;

    /**
     * 未达标主要原因
     */
    @TableField(value = "cause")
    private String cause;

    /**
     * 对比上次报错信息ID
     */
    @TableField(value = "last_cwli_id")
    private Long lastCwliId;

    /**
     * 上次未达标主机数量
     */
    @TableField(value = "last_not_standard_count")
    private String lastNotStandardCount;

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

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
