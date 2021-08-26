package io.shulie.takin.web.biz.pojo.output.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 2:34 下午
 */
@Data
public class ApplicationDsDetailOutput {
    /**
     * 配置id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 存储类型，0：数据库 1：缓存
     */
    private Integer dbType;

    /**
     * 方案类型，0：影子库 1：影子表 2:影子server
     */
    private Integer dsType;

    /**
     * 服务器地址
     */
    private String url;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 配置
     */
    private String config;

    /**
     * 影子库地址
     */
    private String shadowDbUrl;

    /**
     * 影子库用户名
     */
    private String shadowDbUserName;

    /**
     * 影子库密码
     */
    private String shadowDbPassword;

    /**
     * 最小空闲连接数
     */
    private String shadowDbMinIdle;

    /**
     * 最大数据库连接数
     */
    private String shadowDbMaxActive;
}
