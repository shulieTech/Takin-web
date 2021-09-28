package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测数据构建表
 */
@Data
@TableName(value = "t_data_build")
public class DataBuildEntity {
    /**
     * 数据构建id
     */
    @TableId(value = "DATA_BUILD_ID", type = IdType.INPUT)
    private Long dataBuildId;

    /**
     * 应用id
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 影子库表结构脚本构建状态(0未启动 1正在执行 2执行成功 3执行失败)
     */
    @TableField(value = "DDL_BUILD_STATUS")
    private Integer ddlBuildStatus;

    /**
     * 影子库表结构脚本上一次执行成功时间
     */
    @TableField(value = "DDL_LAST_SUCCESS_TIME")
    private LocalDateTime ddlLastSuccessTime;

    /**
     * 缓存预热脚本执行状态
     */
    @TableField(value = "CACHE_BUILD_STATUS")
    private Integer cacheBuildStatus;

    /**
     * 缓存预热脚本上一次执行成功时间
     */
    @TableField(value = "CACHE_LAST_SUCCESS_TIME")
    private LocalDateTime cacheLastSuccessTime;

    /**
     * 基础数据准备脚本执行状态
     */
    @TableField(value = "READY_BUILD_STATUS")
    private Integer readyBuildStatus;

    /**
     * 基础数据准备脚本上一次执行成功时间
     */
    @TableField(value = "READY_LAST_SUCCESS_TIME")
    private LocalDateTime readyLastSuccessTime;

    /**
     * 铺底数据脚本执行状态
     */
    @TableField(value = "BASIC_BUILD_STATUS")
    private Integer basicBuildStatus;

    /**
     * 铺底数据脚本上一次执行成功时间
     */
    @TableField(value = "BASIC_LAST_SUCCESS_TIME")
    private LocalDateTime basicLastSuccessTime;

    /**
     * 数据清理脚本执行状态
     */
    @TableField(value = "CLEAN_BUILD_STATUS")
    private Integer cleanBuildStatus;

    /**
     * 数据清理脚本上一次执行成功时间
     */
    @TableField(value = "CLEAN_LAST_SUCCESS_TIME")
    private LocalDateTime cleanLastSuccessTime;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
