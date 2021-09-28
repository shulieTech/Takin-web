package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测报告业务活动
 */
@Data
@TableName(value = "t_business_activities")
public class BusinessActivitiesEntity {
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
     * 业务流程ID
     */
    @TableField(value = "pessure_process_id")
    private Long pessureProcessId;

    /**
     * 业务活动名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * tps
     */
    @TableField(value = "tps")
    private String tps;

    /**
     * 平均rt
     */
    @TableField(value = "avg_rt")
    private String avgRt;

    /**
     * 请求成功率
     */
    @TableField(value = "success_rate")
    private String successRate;

    /**
     * 压测峰值时刻
     */
    @TableField(value = "peak_time")
    private String peakTime;

    /**
     * 压测峰值TPS
     */
    @TableField(value = "peak_tps")
    private String peakTps;

    /**
     * 压测峰值rt
     */
    @TableField(value = "peak_avg_rt")
    private String peakAvgRt;

    /**
     * 压测峰值请求量
     */
    @TableField(value = "peak_request")
    private String peakRequest;

    /**
     * 压测峰值请求成功率
     */
    @TableField(value = "peak_request_rate")
    private String peakRequestRate;

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
