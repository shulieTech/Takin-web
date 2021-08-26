package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 报告应用列表
 */
@Data
@TableName(value = "t_snapshot_app_info")
public class SnapshotAppInfoEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测任务流量ID
     */
    @TableField(value = "pessure_process_id")
    private Long pessureProcessId;

    /**
     * 应用名称
     */
    @TableField(value = "app_id")
    private String appId;

    /**
     * 应用名称
     */
    @TableField(value = "app_name")
    private String appName;

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
}
