package io.shulie.takin.web.app.conf;

import java.io.File;
import java.util.Set;
import java.util.HashSet;

import io.shulie.takin.sre.common.result.SreResponse;
import io.shulie.takin.web.common.domain.ErrorInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.core.MethodParameter;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.ext.util.WebPluginUtils;
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
        // 处理 TAKIN_AUTHORITY 请求头
        {
            // 声明字符串
            String takinAuthorityHeaderName = "takin-authority";
            // 声明租户
            String takinTenantAuthorityHeaderName = "takin-tenant-authority";
            String accessControlExposeHeaderName = "Access-Control-Expose-Headers";
            // 填充请求头并对外暴露(Chrome安全策略)
            Set<String> headers = new HashSet<>(2);
            HttpHeaders header = response.getHeaders();
            header.set(takinAuthorityHeaderName, WebPluginUtils.checkUserPlugin().toString());
            header.set(takinTenantAuthorityHeaderName, WebPluginUtils.checkTenantPlugin().toString());
            headers.add(takinAuthorityHeaderName);
            headers.add(takinTenantAuthorityHeaderName);
            headers.add(Response.PAGE_TOTAL_HEADER);
            header.set(accessControlExposeHeaderName, String.join(",", headers));
        }
        if (body instanceof Response){
            Object data = ((Response<?>) body).getData();
            ErrorInfo error = ((Response<?>) body).getError();
            Boolean success = ((Response<?>) body).getSuccess();
            String total = ((Response<?>) body).getTotal();
            if (!success){
                return io.shulie.takin.common.beans.response.ResponseResult.fail(error.getMsg(),"");
            }
            if (StringUtils.isNotBlank(total)){
                return io.shulie.takin.common.beans.response.ResponseResult.success(data, Long.parseLong(total));
            }
            return io.shulie.takin.common.beans.response.ResponseResult.success(data);
        }

        if (body instanceof WebResponse
            || body instanceof File
            || body instanceof ResponseResult
            || body instanceof io.shulie.takin.common.beans.response.ResponseResult
            || body instanceof SreResponse) {
            return body;
        }
        if (body instanceof PageInfo) {return Response.success(body);}
        if (body instanceof PagingList) {return Response.successPagingList((PagingList)body);}
        return Response.success(body);
    }
}
