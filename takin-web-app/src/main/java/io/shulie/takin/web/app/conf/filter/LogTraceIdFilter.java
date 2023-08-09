package io.shulie.takin.web.app.conf.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * @author shiyajian
 * create: 2020-09-18
 */
@Slf4j
public class LogTraceIdFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
//        HttpServletRequest servletRequest = (HttpServletRequest) request;
//        log.info(JSON.toJSONString(servletRequest));
        MDC.put("traceId", UUID.randomUUID().toString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
