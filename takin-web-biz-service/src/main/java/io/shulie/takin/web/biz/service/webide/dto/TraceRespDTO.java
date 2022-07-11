package io.shulie.takin.web.biz.service.webide.dto;

import com.pamirs.pradar.log.parser.utils.PradarUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author angju
 * @date 2022/7/8 16:09
 */
@Data
public class TraceRespDTO implements Serializable {
    private static final long serialVersionUID = -2254757841194400711L;
    /**
     * traceId
     */
    private String traceId;


    /**
     * 应用名称
     */
    private String appName;

    /**
     * 服务名称
     */
    private String service;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 中间件名称
     */
    private String middlewareName;

    /**
     * 请求大小，默认为-1
     */
    private Long requestSize;

    /**
     * 响应大小，默认为-1
     */
    private Long responseSize;

    /**
     * 请求内容
     */
    private String request;

    /**
     * 响应内容
     */
    private String response;

    /**
     * 地址ip
     */
    private String hostIp;

    /**
     * 端口号
     */
    private String port;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 结果编码
     */
    private String resultCode;

    /**
     * 调用id(invokeId="0"为入口)
     *
     */
    private String invokeId;

    /**
     * 耗时（ms）
     */
    private Long cost;

    /**
     * 属性（json）
     *
     */
    private String attributes;

    /**
     * 是否压测流量(1:压测压测 0：正式流量)
     */
    private Integer clusterTest;


    /**
     * 开始时间
     */
    private Long startTime;


    /**
     * 备注（可以存放agent上传的callbackMsg）
     */
    private String comment;


    private int[] invokeIdArray;


    public int[] getInvokeIdArray() {
        if (invokeIdArray == null) {
            invokeIdArray = PradarUtils.parseVersion(invokeId);
            if (invokeIdArray.length >= 1) {
                // 修正部分 trace 不是以 0 作根的问题
                invokeIdArray[0] = 0;
            }
        }
        return invokeIdArray;
    }
}
