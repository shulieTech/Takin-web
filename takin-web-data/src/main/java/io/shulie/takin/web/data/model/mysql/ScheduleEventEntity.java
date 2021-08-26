package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_schedule_event")
public class ScheduleEventEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事件类型:1、文件拆分，2、开通机器
     */
    @TableField(value = "event_type")
    private Integer eventType;

    /**
     * 状态：-1，失败，1、处理中，2、完成
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 扩展字段
     */
    @TableField(value = "features")
    private String features;

    /**
     * 完成时间
     */
    @TableField(value = "finish_time")
    private LocalDateTime finishTime;

    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    @TableField(value = "gmt_update")
    private LocalDateTime gmtUpdate;
}
