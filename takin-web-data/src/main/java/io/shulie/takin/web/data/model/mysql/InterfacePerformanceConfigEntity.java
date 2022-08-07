package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 11:28 上午
 */
@Data
@TableName(value = "t_interface_performance_config")
@ToString(callSuper = true)
public class InterfacePerformanceConfigEntity extends TenantBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口调试名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 请求地址或者域名
     */
    @TableField(value = "request_url")
    private String requestUrl;

    /**
     * 请求头
     */
    @TableField(value = "headers")
    private String headers;

    /**
     * cookies
     */
    @TableField(value = "cookies")
    private String cookies;

    /**
     * 请求体
     */
    @TableField(value = "body")
    private String body;

    /**
     * 请求地址或者域名
     */
    @TableField(value = "http_method")
    private String httpMethod;

    /**
     * 响应超时时间
     */
    @TableField(value = "timeout")
    private Integer timeout;

    /**
     * customerId
     */
    @TableField(value = "customer_id")
    private Long customerId;

    /**
     * 是否重定向
     */
    @TableField(value = "is_redirect")
    private Boolean isRedirect;

    /**
     * contentType数据
     */
    @TableField(value = "content_type")
    private String contentType;

    /**
     * 0：未调试，1，调试中
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * contentType数据
     */
    @TableField(value = "entrance_app_name")
    private String entranceAppName;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 软删
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 创建人
     */
    @TableField(value = "creator_id")
    private Long creatorId;

    /**
     * 主键
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 修改人
     */
    @TableField(value = "modifier_id")
    private Long modifierId;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_modified")
    private Date gmtModified;

}
