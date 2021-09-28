package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测报告列表
 */
@Data
@TableName(value = "t_report_list")
public class ReportListEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "second_link_id")
    private Long secondLinkId;

    /**
     * 二级链路的TPS
     */
    @TableField(value = "second_link_tps")
    private Integer secondLinkTps;

    /**
     * 业务链路名称
     */
    @TableField(value = "link_service_name")
    private String linkServiceName;

    /**
     * 基础链路名称
     */
    @TableField(value = "link_basic_name")
    private String linkBasicName;

    /**
     * 基础链路实体
     */
    @TableField(value = "link_basic")
    private String linkBasic;

    /**
     * 压测开始时间
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 压测结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * rt达标标准
     */
    @TableField(value = "rt_standard")
    private Integer rtStandard;

    /**
     * 0:压测完成;1:压测中
     */
    @TableField(value = "status")
    private Integer status;

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
