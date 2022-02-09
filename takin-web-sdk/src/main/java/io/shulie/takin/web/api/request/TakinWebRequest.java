package io.shulie.takin.web.api.request;

import cn.hutool.http.Method;

/**
 * @author caijianying
 */
public class TakinWebRequest {

    private String serverUrl;

    private Object requestData;

    private Method method;

    public TakinWebRequest() {
    }

    public TakinWebRequest(String serverUrl, Object requestData, Method method) {
        this.serverUrl = serverUrl;
        this.requestData = requestData;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }
}
