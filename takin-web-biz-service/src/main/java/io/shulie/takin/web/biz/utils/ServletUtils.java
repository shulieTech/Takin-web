package io.shulie.takin.web.biz.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author shiyajian
 * create: 2020-09-04
 */
public final class ServletUtils {

    private ServletUtils() { /* no instance */ }

    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes)requestAttributes).getResponse();
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes)requestAttributes).getRequest();
    }
}
