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
    private String label;
    /**
     * 请求量
     */
    private int queryPerSecond;
    /**
     * TPS
     */
    private int transactionsPerSecond;
    /**
     * 响应时间
     */
    private double responseTime;
    /**
     * 成功率
     */
    private double successRate;
    /**
     * 中间件类型
     */
    private String middlewareType;
    /**
     * 关联业务活动名称
     */
    private String[] labels;
}
