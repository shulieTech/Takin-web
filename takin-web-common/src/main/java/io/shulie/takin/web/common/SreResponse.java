package io.shulie.takin.web.common;

import lombok.Data;

/**
 * @author zhangz
 * Created on 2023/11/29 17:15
 * Email: zz052831@163.com
 */

@Data
public class SreResponse<T> {
    private T data;
    private boolean success;
    private String errorMsg;
    private Object pagination;

    public static <T> SreResponse<T> success(T data) {
        SreResponse response = new SreResponse();
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static SreResponse fail(String errorMsg) {
        SreResponse response = new SreResponse();
        response.setErrorMsg(errorMsg);
        response.setSuccess(false);
        return response;
    }
}
