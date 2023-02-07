package io.shulie.takin.web.entrypoint.controller.traffic.recorder.service;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request.TrafficRecorderQueryRequest;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.response.TrafficRecorderResponse;


/**
 * @author chenxingxing
 * @date 2023/2/3 3:05 下午
 */
public interface TrafficRecorderService {

    PagingList<TrafficRecorderResponse> queryTrafficRecorder(TrafficRecorderQueryRequest request);
}
