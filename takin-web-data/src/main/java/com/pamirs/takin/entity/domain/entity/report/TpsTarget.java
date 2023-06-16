package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/7/29 下午4:43
 */
@Data
public class TpsTarget {

    private String time;

    private Integer tps;

    private BigDecimal cpu;

    private BigDecimal loading;

    private BigDecimal memory;

    private BigDecimal io;

    private BigDecimal network;

    private BigDecimal gcCount;

    private BigDecimal gcCost;

    private BigDecimal youngGcCount;

    private BigDecimal youngGcCost;

    private BigDecimal fullGcCount;

    private BigDecimal fullGcCost;
}
