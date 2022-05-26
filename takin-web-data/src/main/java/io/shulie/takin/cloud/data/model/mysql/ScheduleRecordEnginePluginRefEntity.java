package io.shulie.takin.cloud.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 调度记录引擎插件引用表
 *
 * @author lipeng
 * @date 2021-01-13 11:24 上午
 */
@Data
@TableName(value = "t_schedule_record_engine_plugins_ref")
public class ScheduleRecordEnginePluginRefEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调度记录id
     */
    @TableField(value = "schedule_record_id")
    private Long scheduleRecordId;

    /**
     * 引擎插件存放目录
     */
    @TableField(value = "engine_plugin_file_path")
    private String enginePluginFilePath;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;
}
