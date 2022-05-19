package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

@Data
public class InterfacePerformanceConfigVO extends UserCommonExt {
    /**
     * 配置Id
     */
    private Long id;
    /**
     * 压测场景名称
     */
    private String name;

    /**
     * get post delete put
     */
    private String httpMethod;

    /**
     * 完整url
     */
    private String requestUrl;

    /**
     * 请求头
     */
    private String headers;

    /**
     * cookies
     */
    private String cookies;

    /**
     * 请求体
     */
    private String body;

    /**
     * 响应超时时间
     */
    private Integer timeout;

    /**
     * 是否重定向
     */
    private Boolean isRedirect;

    /**
     * 入口应用名
     */
    private String entranceAppName;
}
