package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_warn_detail")
public class WarnDetailEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测编号：同压测报告ID
     */
    @TableField(value = "pt_id")
    private Long ptId;

    /**
     * SLA配置ID
     */
    @TableField(value = "sla_id")
    private Long slaId;

    /**
     * SLA配置名称
     */
    @TableField(value = "sla_name")
    private String slaName;

    /**
     * 业务活动ID
     */
    @TableField(value = "business_activity_id")
    private Long businessActivityId;

    /**
     * 业务活动名称
     */
    @TableField(value = "business_activity_name")
    private String businessActivityName;

    /**
     * 告警内容
     */
    @TableField(value = "warn_content")
    private String warnContent;

    @TableField(value = "warn_rule_detail")
    private String warnRuleDetail;

    /**
     * 压测实际值
     */
    @TableField(value = "real_value")
    private Double realValue;

    /**
     * 告警时间
     */
    @TableField(value = "warn_time")
    private LocalDateTime warnTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

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
