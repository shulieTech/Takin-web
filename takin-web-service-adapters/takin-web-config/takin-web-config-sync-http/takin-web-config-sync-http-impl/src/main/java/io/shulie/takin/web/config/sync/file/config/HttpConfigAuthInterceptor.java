package io.shulie.takin.web.config.sync.file.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author shiyajian
 * create: 2020-10-02
 */
@Component
public class HttpConfigAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        // TODO  此处需要对agent的请求进行鉴权
        return true;
    }
}
