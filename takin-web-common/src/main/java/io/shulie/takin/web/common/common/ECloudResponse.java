package io.shulie.takin.web.common.common;

import lombok.Data;

@Data
public class ECloudResponse {

    private boolean result;

    private String errmsg;

    private int code = 200;

    private String msg;
}
