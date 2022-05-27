package io.shulie.takin.web.biz.service.interfaceperformance;

import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDebugRequest;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:45 下午
 */
public interface PerformanceDebugService {
    /**
     * @param request
     */
    String debug(PerformanceDebugRequest request);

    /**
     * @param request
     */
    String simple_debug(PerformanceDebugRequest request);
}
