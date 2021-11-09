package io.shulie.takin.web.data.result.application;

import java.util.Date;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 10:49 上午
 */
@Data
public class ApplicationDsResult {
    private Long id;

    /**
     * 应用主键
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 存储类型 0:数据库 1:缓存
     */
    private Integer dbType;

    /**
     * 方案类型 0:影子库 1:影子表 2:影子server
     */
    private Integer dsType;

    /**
     * 数据库url,影子表需填
     */
    private String url;

    /**
     * xml配置
     */
    private String config;

    /**
     * 解析后配置
     */
    private String parseConfig;

    /**
     * 状态 0:启用；1:禁用
     */
    private Integer status;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

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
    private Integer isDeleted;

    public String getFilterStr() {
        return this.getUrl() + "@@" + "" + "@@" + "";
    }
}
