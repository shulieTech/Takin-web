package io.shulie.takin.cloud.common.bean.collector;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 事件发送对象
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 15:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SendMetricsEvent extends Metrics {

    private Long sceneId;
    private Long reportId;
    private Long tenantId;

}
