package io.shulie.takin.web.entrypoint.controller.health;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class SampleMvcInterceptor extends HandlerInterceptorAdapter {
    private static final Counter COUNTER =
        Counter.builder("Http请求统计")
            .tag("HttpCount", "HttpCount")
            .description("Http请求统计")
            .register(Metrics.globalRegistry);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        COUNTER.increment();
    }
}
