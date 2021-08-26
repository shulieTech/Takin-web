package io.shulie.takin.web.common.vo.agent;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/1/21 11:21 上午
 */
@Data
public class TraceVO {

    private String traceDeployObject;
    /**
     * 行号
     */
    private Integer lineNum;

    private List<Long> costs;
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
    private BigDecimal min;
    private BigDecimal max;

    /**
     * 下一级的信息
     */
    private List<TraceVO> children;

}
