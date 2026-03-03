package io.shulie.takin.web.app.leqi;

import java.util.Map;

public class HttpResponse {

    private String httpId;
    private Integer httpCode;
    private Map<String, String> headers;

    private byte[] Content;

    public String getHttpId() {
        return httpId;
    }

    public void setHttpId(String httpId) {
        this.httpId = httpId;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getContent() {
        return Content;
    }

    public void setContent(byte[] content) {
        Content = content;
    }

}

