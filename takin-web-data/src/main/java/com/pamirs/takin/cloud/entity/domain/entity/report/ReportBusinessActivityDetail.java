package com.pamirs.takin.cloud.entity.domain.entity.report;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @author -
 */
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
     * 应用IDs
     */
    private String applicationIds;

    /**
     * 业务请求
     */
    private String bindRef;

    /**
     * 请求数
     */
    private Long request;

    /**
     * 平均并发数
     */
    private BigDecimal avgConcurrenceNum;

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
     * 目标tps
     */
    private BigDecimal targetTps;

    /**
     * 目标rt
     */
    private BigDecimal targetRt;

    /**
     * 目标成功率
     */
    private BigDecimal targetSuccessRate;

    /**
     * 目标sa
     */
    private BigDecimal targetSa;

    /**
     * 最大tps
     */
    private BigDecimal maxTps;

    private BigDecimal minTps;

    /**
     * 最大rt
     */
    private BigDecimal maxRt;

    /**
     * 最小rt
     */
    private BigDecimal minRt;

    /**
     * 是否通过
     */
    private Integer passFlag;

    /**
     * RT分布
     */
    private String rtDistribute;

    private String features;

    private Integer isDeleted;

    private Date gmtCreate;

    private Date gmtUpdate;

    /**
     * 诊断ID
     */
    private Long diagnosisId;
}
