package io.shulie.takin.web.biz.pojo.input.sresla;

import lombok.Data;

import java.io.Serializable;

@Data
public class SlaParamResponse implements Serializable {

    private String chainCode;

    private String tenantCode;

    private String service;

    private String method;

    private String appName;

    private String slaCode;

    private String slaConfig;
}
