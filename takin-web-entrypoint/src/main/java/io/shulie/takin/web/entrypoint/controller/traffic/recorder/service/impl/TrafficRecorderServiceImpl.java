package io.shulie.takin.web.entrypoint.controller.traffic.recorder.service.impl;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.request.TrafficRecorderQueryRequest;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.pojo.response.TrafficRecorderResponse;
import io.shulie.takin.web.entrypoint.controller.traffic.recorder.service.TrafficRecorderService;
import org.springframework.stereotype.Service;

/**
 * @author chenxingxing
 * @date 2023/2/3 3:06 下午
 */
@Service
public class TrafficRecorderServiceImpl implements TrafficRecorderService {

    @Override
    public PagingList<TrafficRecorderResponse> queryTrafficRecorder(TrafficRecorderQueryRequest request) {
        // 1.获取当前租户，组装租户查询条件
        // 2.根据应用获取机房信息
        // 3.生成流量任务
        // 4.返回响应结果
        return null;
    }
}
