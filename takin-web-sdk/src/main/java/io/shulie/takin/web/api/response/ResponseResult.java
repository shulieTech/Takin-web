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
    private String errorMsg;

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

    public ResponseResult(String errorMsg, String code, T data) {
        this.errorMsg = errorMsg;
        this.code = code;
        this.data = data;
    }

    public ResponseResult(String errorMsg) {
        this(errorMsg,"500",null);
    }

    public ResponseResult(T data) {
        this(null,"200",data);
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
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

    public static ResponseResult success(){
        ResponseResult result = new ResponseResult();
        result.setCode("200");
        return result;
    }

    public static <T> ResponseResult<T> success(T data){
        return new ResponseResult<>(data);
    }

    public static ResponseResult fail(String errorMsg){
        return new ResponseResult<>(errorMsg);
    }
}
