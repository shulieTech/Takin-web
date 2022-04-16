package io.shulie.takin.cloud.biz.service.sla;

import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.cloud.model.callback.Sla;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/4/20 下午4:47
 */
public interface SlaService {
    void detection(List<Sla.SlaInfo> slaInfo);
}
