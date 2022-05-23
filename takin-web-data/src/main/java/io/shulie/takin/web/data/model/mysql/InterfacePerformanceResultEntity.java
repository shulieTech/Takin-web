package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 11:28 上午
 */
@Data
@TableName(value = "t_interface_performance_result")
@ToString(callSuper = true)
public class InterfacePerformanceResultEntity extends TenantBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口压测配置Id
     */
    @TableField(value = "config_id")
    private Long configId;

    /**
     * 请求地址或者域名
     */
    @TableField(value = "request_url")
    private String requestUrl;

    /**
     * 请求地址或者域名
     */
    @TableField(value = "http_method")
    private String httpMethod;

    /**
     * 请求地址或者域名
     */
    @TableField(value = "request")
    private String request;

    /**
     * 响应
     */
    @TableField(value = "response")
    private String response;

    /**
     * 响应状态码
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 错误信息
     */
    @TableField(value = "error_message")
    private String errorMessage;

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
