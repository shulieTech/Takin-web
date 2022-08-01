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

    /**
     * 调试，走脚本调试功能
     *
     * @param request
     */
    String simple_debug_ext(PerformanceDebugRequest request);

    /**
     * 启动调试
     *
     * @param request
     * @return
     */
    String start(PerformanceDebugRequest request);
}
