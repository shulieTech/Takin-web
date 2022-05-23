package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class PerformanceResultResponse {
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口压测配置Id
     */
    private Long configId;

    /**
     * 请求地址或者域名
     */
    private String requestUrl;

    /**
     * 请求地址或者域名
     */
    private String httpMethod;

    /**
     * 请求地址或者域名
     */
    private String request;

    /**
     * 响应
     */
    private String response;

    /**
     * 响应状态码
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;
}
