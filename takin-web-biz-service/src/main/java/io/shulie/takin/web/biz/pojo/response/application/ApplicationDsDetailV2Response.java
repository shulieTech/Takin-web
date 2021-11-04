package io.shulie.takin.web.biz.pojo.response.application;

import java.io.Serializable;

/**
 * @Author: 南风
 * @Date: 2021/8/31 10:44 上午
 */
public class ApplicationDsDetailV2Response implements Serializable {

    /**
     * 配置id
     */
    private Long id;

    /**
     * 中间件类型
     */
    private String middlewareType;

    /**
     * 中间件名称
     */
    private String connectionPool;

    /**
     * 业务数据源
     */
    private String url;

    /**
     * 隔离类型
     */
    private String dsType;

    /**
     * 缓存模式
     */
    private String cacheType;

    




}
