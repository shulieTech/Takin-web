package com.pamirs.takin.entity.domain.entity;

import java.io.Serializable;
import java.util.Date;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * t_application_api_manage
 *
 * @author
 */
@Data
public class ApplicationApiManage extends TenantCommonExt implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * api
     */
    private String api;
    /**
     * 应用主键
     */
    private Long applicationId;
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Byte isDeleted;

    private Long userId;

    private String requestMethod;

    private Integer isAgentRegiste;

    /**
     * 没有这个字段插入会抛错
     */
    private Long customerId;
}
