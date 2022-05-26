package io.shulie.takin.web.data.result.tracemanage;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author shulie
 */
@Data
public class TraceManageDeployResult extends ContextExt {

    private Long id;

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
     * 平均耗时
     */
    private BigDecimal avgCost;

    /**
     * 中位数
     */
    private BigDecimal p50;
    private BigDecimal p90;
    private BigDecimal p95;
    private BigDecimal p99;
    private Integer min;
    private Integer max;

    /**
     * 状态0:待采集;1:采集中;2:采集结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 拓展字段
     */
    private String feature;

    /**
     * 该追踪实例的子集
     */
    private List<TraceManageDeployResult> children;
}
