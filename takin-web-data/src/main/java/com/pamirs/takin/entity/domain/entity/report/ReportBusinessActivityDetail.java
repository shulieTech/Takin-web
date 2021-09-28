package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ReportBusinessActivityDetail {
    private Long id;

    /**
     * 报告ID
     */
    private Long reportId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 业务活动ID
     */
    private Long businessActivityId;

    /**
     * 业务活动名称
     */
    private String businessActivityName;

    /**
     * 请求数
     */
    private Long request;

    /**
     * tps
     */
    private BigDecimal tps;

    /**
     * rt
     */
    private BigDecimal rt;

    /**
     * 成功率
     */
    private BigDecimal successRate;

    /**
     * sa
     */
    private BigDecimal sa;

    /**
     * 最大tps
     */
    private BigDecimal maxTps;

    /**
     * 最大rt
     */
    private BigDecimal maxRt;

    /**
     * 最小rt
     */
    private BigDecimal minRt;

    private String features;

    private Integer isDeleted;

    private Date gmtCreate;

    private Date gmtUpdate;

    /**
     * 租户id
     */
    private long tenantId;
    /**
     * 用户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
