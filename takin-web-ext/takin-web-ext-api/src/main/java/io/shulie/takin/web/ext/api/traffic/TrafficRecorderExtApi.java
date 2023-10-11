package io.shulie.takin.web.ext.api.traffic;

import io.shulie.takin.web.ext.entity.traffic.TrafficRecorderExtResponse;
import io.shulie.takin.web.ext.entity.traffic.TrafficRecorderQueryExt;
import org.pf4j.ExtensionPoint;

import java.util.List;

public interface TrafficRecorderExtApi  extends ExtensionPoint {

    /**
     * 获取流量数据
     * @param queryExt
     * @return
     */
    List<TrafficRecorderExtResponse> listTrafficRecorder(TrafficRecorderQueryExt queryExt);

    /**
     * 获取模板用的数据
     * @param queryExt
     * @return
     */
    List<TrafficRecorderExtResponse> getTemplate(TrafficRecorderQueryExt queryExt);

    /**
     * 获取模板用 数量
     * @param queryExt
     * @return
     */
    Long getTemplateCount(TrafficRecorderQueryExt queryExt);

}
