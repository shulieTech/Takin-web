package com.pamirs.takin.entity.domain.entity.collector;

import lombok.Data;

/**
 * 事件发送对象
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.takin.log.entity
 * @date 2020-04-20 15:20
 */
@Data
public class SendMetricsEvnet extends Metrics {

    private String scenId;
    private String reportId;

}
