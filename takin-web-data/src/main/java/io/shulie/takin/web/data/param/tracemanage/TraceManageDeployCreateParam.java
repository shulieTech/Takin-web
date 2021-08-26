package io.shulie.takin.web.data.param.tracemanage;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageDeployCreateParam {


    /**
     * 追踪对象id
     */
    private Long traceManageId;

    /**
     * 追踪对象实例名称
     */
    private String traceDeployObject;

    /**
     * 追踪凭证
     */
    private String sampleId;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 0:没有;1:有;2未知
     */
    private Integer hasChildren;

    /**
     * 行号
     */
    private Integer lineNum;

    /**
     * 评价耗时
     */
    private BigDecimal avgCost;

    /**
     * 中位数
     */
    private BigDecimal p50;
    private BigDecimal p90;
    private BigDecimal p95;
    private BigDecimal p99;
    private BigDecimal min;
    private BigDecimal max;


    /**
     * 状态0:待采集;1:采集中;2:采集结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 拓展字段
     */
    private String feature;
}
