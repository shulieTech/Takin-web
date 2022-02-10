package io.shulie.takin.web.api.response;

import java.io.Serializable;

/**
 * @author caijianying
 */

public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 45387487319877475L;
    /**
     * 错误信息
     */
    private ErrorInfo error;

    /**
     * 状态码
     */
    private String code;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 是否成功
     */
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ResponseResult() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }
}
