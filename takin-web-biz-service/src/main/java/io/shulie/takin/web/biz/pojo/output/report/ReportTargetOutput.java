package io.shulie.takin.web.biz.pojo.output.report;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReportTargetOutput implements Serializable {

    private Long reportId;

    private String pressureTestTime;

    private Long requestCount;

    private Integer concurrent;

    private BigDecimal avgTps;

    private BigDecimal maxTps;

    private BigDecimal minTps;

    private BigDecimal successRate;

    private BigDecimal sa;

    private String startTime;

    private BigDecimal rt50;

    private BigDecimal rt75;

    private BigDecimal rt90;

    private BigDecimal rt95;

    private BigDecimal rt99;
}
