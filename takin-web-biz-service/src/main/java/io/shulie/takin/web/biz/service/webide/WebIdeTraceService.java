package io.shulie.takin.web.biz.service.webide;

import io.shulie.takin.web.biz.service.webide.dto.TraceRespDTO;

import java.util.List;

/**
 * @author angju
 * @date 2022/7/8 15:33
 */
public interface WebIdeTraceService {

    /**
     * 根据业务活动id获取最新的trace详情
     * @param businessActivityId
     * @return
     */
    List<TraceRespDTO> getLastTraceDetailByBusinessActivityId(Long businessActivityId);


    /**
     * 根据traceId获取trace详情
     * @param traceId
     * @return
     */
    List<TraceRespDTO> getTraceDetailByTraceId(String traceId);
}
