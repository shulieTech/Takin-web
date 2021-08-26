package io.shulie.takin.web.amdb.bean.result.trace;

import java.io.Serializable;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
@Data
public class EntryTraceInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String appName;

    private String entry;

    private String method;

    private String status;

    /**
     * 请求时间 时间戳
     */
    private Long startTime;

    private Long endTime;

    private Long processTime;

    private String traceId;

    /**
     * resultCode: 0: 成功 1:失败 2:dubbo 错误 3:超时 4:未知 5:断言失败
     */
    private String resultCode;

    /**
     * 入口服务
     */
    private String serviceName;

    /**
     * 请求体
     */
    private String request;

    /**
     * 响应体
     */
    private String response;

    /**
     * 断言结果
     */
    private String assertResult;

    /**
     * 端口
     */
    private String port;

    /**
     * 消耗时间
     */
    private Long cost;

    /**
     * 远程ip
     */
    private String remoteIp;

    /**
     * 方法名称
     */
    private String methodName;

}
