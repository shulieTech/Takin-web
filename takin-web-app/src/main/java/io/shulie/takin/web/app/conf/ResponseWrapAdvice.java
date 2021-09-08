package io.shulie.takin.web.app.conf;

import java.io.File;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.PageInfo;
import org.springframework.http.MediaType;
import org.springframework.core.MethodParameter;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.domain.WebResponse;
import org.springframework.http.server.ServerHttpRequest;
import com.pamirs.takin.common.ResponseOk.ResponseResult;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author shiyajian
 * create: 2020-09-24
 */
@ControllerAdvice(basePackages = "io.shulie.takin")
@Slf4j
public class ResponseWrapAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter parameter,
        @NonNull Class<? extends HttpMessageConverter<?>> converter) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converter);
    }

    @Override
    public Object beforeBodyWrite(Object body,
        @NonNull MethodParameter parameter, @NonNull MediaType mediaType,
        @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
        @NonNull ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Response
            || body instanceof WebResponse
            || body instanceof File
            || body instanceof ResponseResult
            || body instanceof io.shulie.takin.common.beans.response.ResponseResult) {
            return body;
        }
        if (body instanceof PageInfo) {return Response.success(body);}
        if (body instanceof PagingList) {return Response.successPagingList((PagingList)body);}
        return Response.success(body);
    }
}
