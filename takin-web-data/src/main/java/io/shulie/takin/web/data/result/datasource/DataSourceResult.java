package io.shulie.takin.web.data.result.datasource;

import java.util.Date;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/30 10:11 上午
 */
@Data
public class DataSourceResult extends TenantCommonExt {
    private Long id;

    /**
     * 0:MYSQL
     */
    private Integer type;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源地址
     */
    private String jdbcUrl;

    /**
     * 数据源用户
     */
    private String username;

    /**
     * 数据源密码
     */
    private String password;

    /**
     * 扩展字段，k-v形式存在
     */
    private String features;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否有效 0:有效;1:无效
     */
    private Boolean isDeleted;
}
