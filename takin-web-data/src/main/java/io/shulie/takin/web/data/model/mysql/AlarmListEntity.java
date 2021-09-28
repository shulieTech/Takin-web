package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 告警列表
 */
@Data
@TableName(value = "t_alarm_list")
public class AlarmListEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * WAR包名
     */
    @TableField(value = "war_packages")
    private String warPackages;

    /**
     * IP
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 告警汇总
     */
    @TableField(value = "alarm_collects")
    private String alarmCollects;

    /**
     * 告警时间
     */
    @TableField(value = "alarm_date")
    private LocalDateTime alarmDate;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 是否已删除
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
