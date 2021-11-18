package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * trace入口列表
 */
@Data
@TableName(value = "t_takin_trace_entry")
public class TakinTraceEntryEntity extends TenantBaseEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应用
     */
    @TableField(value = "app_name")
    private String appName;

    /**
     * 入口
     */
    @TableField(value = "entry")
    private String entry;

    /**
     * 方法
     */
    @TableField(value = "method")
    private String method;

    /**
     * 状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Long startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Long endTime;

    /**
     * 耗时
     */
    @TableField(value = "process_time")
    private Long processTime;

    /**
     * traceId
     */
    @TableField(value = "trace_id")
    private String traceId;
}
