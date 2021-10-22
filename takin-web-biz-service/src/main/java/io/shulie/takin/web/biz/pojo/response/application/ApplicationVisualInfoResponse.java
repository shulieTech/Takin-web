package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.web.biz.pojo.response.activity.ActivityBottleneckResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ApplicationVisualInfoResponse implements Serializable {

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
    /**
     * 关联业务活动名称
     */
    private Map activeIdAndName;
    /**
     * 是否关注
     */
    private boolean isAttend;
    /**
     * 健康检查结果
     */
    private ActivityBottleneckResponse response;
}
