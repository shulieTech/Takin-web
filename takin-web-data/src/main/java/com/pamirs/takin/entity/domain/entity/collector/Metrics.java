package com.pamirs.takin.entity.domain.entity.collector;

import lombok.Data;

/**
 * Influxdb 对象
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.takin.log.entity
 * @date 2020-04-20 15:20
 */
@Data
public class Metrics {

    /**
     * tag
     */
    private String transaction;

    /**
     * field
     */
    private Integer count;
    private Integer failCount;
    private Double avgTps;
    private Double avgRt;
    private Double successRate;
    private Double sa;
    private Double maxRt;
    private Double minRt;
    private long timestamp;

}
