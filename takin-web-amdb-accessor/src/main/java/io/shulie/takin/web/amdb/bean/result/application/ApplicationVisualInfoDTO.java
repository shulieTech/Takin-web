package io.shulie.takin.web.amdb.bean.result.application;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplicationVisualInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    private String appName;
    /**
     * 服务名称
     */
    private String serviceAndMethod;
    /**
     * 请求量
     */
    private int requestCount;
    /**
     * TPS
     */
    private int tps;
    /**
     * 响应时间
     */
    private double responseConsuming;
    /**
     * 成功率
     */
    private double successRatio;
    /**
     * 中间件类型
     */
    private String middlewareName;
    /**
     * 关联业务活动名称
     */
    private String[] activeList;
}
