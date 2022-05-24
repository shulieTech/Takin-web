package io.shulie.takin.web.common.vo.interfaceperformance;

import lombok.Data;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 3:05 下午
 */
@Data
public class PerformanceConfigVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口调试名称
     */
    private String name;

    /**
     * 请求地址或者域名
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
     * 请求地址或者域名
     */
    private String httpMethod;

    /**
     * 响应超时时间
     */
    private Integer timeout;

    /**
     * customerId
     */
    private Long customerId;

    /**
     * 是否重定向
     */
    private Boolean isRedirect;

    /**
     * contentType数据
     */
    private String contentType;

    /**
     * 0：未调试，1，调试中
     */
    private Integer status;

    /**
     * 关联入口应用
     */
    private String entranceAppName;

    /**
     * 软删
     */
    private Boolean isDeleted;

    /**
     * 创建人
     */
    private Long creatorId;

    /**
     * 修改人
     */
    private Long modifierId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;
}
