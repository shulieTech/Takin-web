package com.pamirs.takin.cloud.entity.domain.dto.report;

import io.shulie.takin.cloud.common.utils.NumberUtil;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 莫问
 * @date 2020-04-21
 */
@Data
public class StatReportDTO {

    /**
     * 时间
     */
    private String time;

    /**
     * 总请求
     */
    private Long totalRequest;

    /**
     * 5s请求数
     */
    private Long tempRequestCount;

    /**
     * 失败的总次数
     */
    private Long failRequest;

    /**
     * 平均线程数
     */
    private BigDecimal avgConcurrenceNum;

    /**
     * 最大并发数
     */
    private Integer maxConcurrenceNum;

    /**
     * tps
     */
    private BigDecimal tps;

    /**
     * 平均rt
     */
    private BigDecimal avgRt;

    /**
     * Sa总计数
     */
    private BigDecimal saCount;

    /**
     * minRt
     */
    private BigDecimal minRt;

    /**
     * maxRt
     */
    private BigDecimal maxRt;

    /**
     * maxTps
     */
    private BigDecimal maxTps;

    /**
     * 查询记录数
     */
    private Long recordCount;

    private BigDecimal minTps;
    /**
     * 获取SA
     * sa总数/总请求*100
     *
     * @return -
     */
    public BigDecimal getSa() {
        return BigDecimal.valueOf(NumberUtil.getPercentRate(saCount, getTempRequestCount()));
    }

    /**
     * 成功率
     * (总次数-失败次数)/总次数*100
     *
     * @return -
     */
    public BigDecimal getSuccessRate() {
        if (null == getTempRequestCount()) {
            return null;
        }
        if (null == getFailRequest()) {
            return BigDecimal.valueOf(100);
        }
        return BigDecimal.valueOf(NumberUtil.getPercentRate(getTempRequestCount() - getFailRequest(), getTempRequestCount()));
        //fixed end
    }

}
