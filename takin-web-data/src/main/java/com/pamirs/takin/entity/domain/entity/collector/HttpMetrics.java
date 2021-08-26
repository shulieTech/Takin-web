package com.pamirs.takin.entity.domain.entity.collector;

import lombok.Data;

/**
 * 接收http Metrics
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.domain.entity.collector
 * @date 2020-04-20 19:24
 */
@Data
public class HttpMetrics {

    long timestamp;
    private String transaction;
    private Integer count;
    private Integer failCount;
    private Double rt;
    private Integer saCount;
    private Double maxRt;
    private Double minRt;
}
