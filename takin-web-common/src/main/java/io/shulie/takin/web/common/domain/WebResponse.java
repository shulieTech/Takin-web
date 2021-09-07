package io.shulie.takin.web.common.domain;

import java.util.List;
import java.util.HashMap;
import java.io.Serializable;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.web.common.common.Response;

/**
 * @author vernon
 * @date 2019/9/20 17:56
 * @deprecated 未来都不需要返回这个了，controller层会自动封装
 */
@Deprecated
public class WebResponse<T> implements Serializable {

    /**
     * 数据总条数在 header 中返回，这是 header 名称
     */
    public static final String PAGE_TOTAL_HEADER = "x-total-count";
    public static final boolean DEFAULT_SUCCESS = true;
    private static final long serialVersionUID = -1975641525974734365L;
    /**
     * 错误信息实体
     */
    private ErrorInfo error;
    /**
     * 返回数据，如果请求失败，则为空
     */
    private T data;
    /**
     * 成功标记
     */
    private Boolean success;

    public WebResponse() {}

    public WebResponse(T data) {
        this(null, data, DEFAULT_SUCCESS);
    }

    public WebResponse(ErrorInfo error, boolean success) {
        this(error, null, success);
    }

    public WebResponse(ErrorInfo error, T data, boolean success) {
        this.error = error;
        this.data = data;
        this.success = success;
    }

    public WebResponse(ErrorInfo error, T data, boolean success, int code) {
        this.error = error;
        this.data = data;
        this.success = success;
    }

    /**
     * 返回成功,无内容
     *
     * @param <T>
     * @return
     */
    public static <T> WebResponse<T> success() {
        return new WebResponse<>(null);
    }

    /**
     * 返回成功
     *
     * @return
     */
    public static <T> WebResponse<T> success(T data) {
        if (data instanceof PageInfo) {
            return success(data);
        }
        return new WebResponse<>(data);
    }

    public static <T> WebResponse<List<T>> success(PageInfo<T> data) {
        Response.setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, data.getTotal() + "");}});
        return new WebResponse<>(data.getList());
    }

    public static <T> WebResponse<List<T>> success(List<T> data, long total) {
        Response.setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(total));}});
        return new WebResponse<>(data);
    }

    /**
     * 返回失败，使用传入的错误码
     */
    public static WebResponse fail(String code, String msgTemplate, Object... args) {
        ErrorInfo errorInfo = ErrorInfo.build(code, msgTemplate, args);
        return new WebResponse<>(errorInfo, false);
    }

    /**
     * 返回失败，使用传入的错误码
     */
    public static WebResponse fail(String msgTemplate, Object... args) {
        ErrorInfo errorInfo = ErrorInfo.build("500", msgTemplate, args);
        return new WebResponse<>(errorInfo, false);
    }

    public WebResponse setTotal(Long total) {
        Response.setHeaders(new HashMap<String, String>(1) {{put(PAGE_TOTAL_HEADER, String.valueOf(total));}});
        return this;
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

}
