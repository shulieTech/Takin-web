package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 出数系统
 */
@Data
@TableName(value = "pradar_app_bamp")
public class PradarAppBampEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 埋点ip
     */
    @TableField(value = "app_point_id")
    private Long appPointId;

    /**
     * 间隔，单位min
     */
    @TableField(value = "bamp_interval")
    private Integer bampInterval;

    /**
     * 指标编码
     */
    @TableField(value = "index_code")
    private String indexCode;

    /**
     * 响应耗时
     */
    @TableField(value = "rt_avg")
    private Integer rtAvg;

    @TableField(value = "deleted")
    private Boolean deleted;

    @TableField(value = "gmt_created")
    private LocalDateTime gmtCreated;

    @TableField(value = "gmt_modified")
    private LocalDateTime gmtModified;
}
