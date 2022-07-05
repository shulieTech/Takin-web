package com.pamirs.takin.common.util;


import io.shulie.takin.utils.string.StringUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: vernon
 * @Date: 2022/3/10 19:01
 * @Description:
 */
public class ResponseWrapper implements Serializable {


    public static final ResponseWrapper IGNORE = new ResponseWrapper();

    public static final ResponseWrapper Error() {
        return build();
    }

    private String data;
    private Map<String, String> header;

    private Integer status;

    public static ResponseWrapper build() {
        return new ResponseWrapper();
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getData() {
        return data;
    }

    public ResponseWrapper setData(String data) {
        this.data = data;
        return this;
    }

    public ResponseWrapper setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public ResponseWrapper setStatus(String status) {
        this.status = StringUtil.isEmpty(status) ? 200 : Integer.parseInt(status);
        return this;
    }
}
