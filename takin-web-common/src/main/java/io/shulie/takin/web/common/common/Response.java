package io.shulie.takin.web.common.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.domain.ErrorInfo;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author vernon
 * @date 2019/9/20 17:56
 * @deprecated 默认响应格式包装类
 */
@Slf4j
public class Response<T> {

    /**
     * 数据总条数在 header 中返回，这是 header 名称
     */
    public static final String PAGE_TOTAL_HEADER = "x-total-count";
    public static final boolean DEFAULT_SUCCESS = true;
    /**
     * 错误信息实体
     */
    private ErrorInfo error;
    /**
     * 返回数据，如果请求失败，则为空
     */
    @ApiModelProperty(name = "data", value = "返回的具体数据")
    private T data;
    /**
     * 成功标记
     */
    @ApiModelProperty(name = "success", value = "是否成功")
    private Boolean success;

    private Boolean unsuccess;

    public Response(T data) {
        this(null, data, DEFAULT_SUCCESS);
    }

    public Response(ErrorInfo error, boolean success) {
        this(error, null, success);
    }

    public Response(ErrorInfo error, T data, boolean success) {
        this.error = error;
        this.data = data;
        this.success = success;
    }

    public Response(ErrorInfo error, T data, boolean success, int code) {
        this.error = error;
        this.data = data;
        this.success = success;
    }

    public static Response failByType(String code, String msgTemplate, String headerType) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMsgTemplate(msgTemplate);
        setHeaders(new HashMap<String, String>(1) {{put("type", headerType);}});
        return new Response(errorInfo, null, false, Integer.parseInt(code));
    }

    /**
     * 返回成功,无内容
     *
     * @return -
     */
    public static <T> Response<T> success() {
        return new Response<>(null);
    }

    /**
     * 返回成功
     *
     * @return -
     */
    public static <T> Response<T> success(T data) {
        if (data instanceof PageInfo) {
            return success(data);
        }
        if (data instanceof List) {
            return (Response<T>)successList((List)data);
        }
        return new Response<>(data);
    }

    public static Response<List> successList(List data) {
        permissionHook(data);
        return new Response<>(data);
    }

    public static <T> Response<List<T>> success(PageInfo<T> data) {
        setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(data.getTotal()));}});
        permissionHook(data);
        return new Response<>(data.getList());
    }

    private static <T> void permissionHook(List<T> list) {
        if (list == null) {
            return;
        }
        for (T responseObj : list) {
            if (responseObj instanceof AuthQueryResponseCommonExt) {
                AuthQueryResponseCommonExt commonExt = (AuthQueryResponseCommonExt)responseObj;
                commonExt.permissionControl();
            }
        }
    }

    private static <T> void permissionHook(PageInfo<T> data) {
        permissionHook(data.getList());
    }

    public static <T> Response<List<T>> successPagingList(PagingList<T> data) {
        setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(data.getTotal()));}});
        return new Response<>(data.getList());
    }

    public static <T> Response<List<T>> success(List<T> data, long total) {
        setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(total));}});
        return new Response<>(data);
    }

    /**
     * 返回失败，使用传入的错误码
     */
    public static Response fail(String code, String msgTemplate, Object... args) {
        OperationLogContextHolder.ignoreLog();
        ErrorInfo errorInfo = ErrorInfo.build(code, msgTemplate, args);
        HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        assert response != null;
        response.setStatus(Integer.parseInt(code));
        return new Response<>(errorInfo, false);
    }

    /**
     * 返回失败，使用传入的错误码
     */
    public static Response fail(String msgTemplate, Object... args) {
        OperationLogContextHolder.ignoreLog();
        ErrorInfo errorInfo = ErrorInfo.build("500", msgTemplate, args);
        HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        assert response != null;
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return new Response<>(errorInfo, false);
    }

    public Response setTotal(Long total) {
        setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(total));}});
        return this;
    }

    public String getTotal() {
        return getHeaders(PAGE_TOTAL_HEADER);
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    private static final String TAKIN_AUTHORITY = "takin-authority";

    /**
     * 设置响应头<br/>最后统一暴露自定义响应头
     *
     * @param data 响应头
     */
    public static void setHeaders(Map<String, String> data) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)requestAttributes;
        if (servletRequestAttributes != null) {
            HttpServletResponse response = servletRequestAttributes.getResponse();
            if (response != null) {
                data.forEach(response::setHeader);
            }
            log.debug("设置响应头失败,servletRequestAttributes.getResponse()=null");
        }
        log.debug("设置响应头失败,(ServletRequestAttributes)requestAttributes=null");
    }

    /**
     * 设置响应头<br/>最后统一暴露自定义响应头
     *
     */
    public static String getHeaders(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)requestAttributes;
        if (servletRequestAttributes != null) {
            HttpServletResponse response = servletRequestAttributes.getResponse();
            if (response != null) {
               return response.getHeader(key);
            }
            log.debug("设置响应头失败,servletRequestAttributes.getResponse()=null");
        }
        log.debug("设置响应头失败,(ServletRequestAttributes)requestAttributes=null");
        return null;
    }
}
