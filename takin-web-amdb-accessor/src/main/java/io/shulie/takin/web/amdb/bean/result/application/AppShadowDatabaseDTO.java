package io.shulie.takin.web.amdb.bean.result.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 南风
 * @Date: 2021/8/27 3:46 下午
 */
@Data
public class AppShadowDatabaseDTO implements Serializable {

    @JsonProperty("id")
    private Long id;

    /**
     * 应用名称
     */
    @JsonProperty("appName")
    private String appName;

    /**
     * 业务数据源
     */
    @JsonProperty("dataSource")
    private String dataSource;

    /**
     * 用户名
     */
    @JsonProperty("tableUser")
    private String tableUser;

    /**
     * 密码
     */
    @JsonProperty("password")
    private String password;

    /**
     * 中间件类型
     */
    @JsonProperty("middlewareType")
    private String middlewareType;

    /**
     * 连接池名称
     */
    @JsonProperty("connectionPool")
    private String connectionPool;

    /**
     * 附加信息
     */
    @JsonProperty("extInfo")
    private String extInfo;

    /**
     * 类型
     */
    @JsonProperty("type")
    private String type;

    /**
     * 动态配置
     */
    @JsonProperty("attachment")
    private String attachment;

    @JsonProperty("gmtCreate")
    private Date gmtCreate;

    @JsonProperty("gmtModify")
    private Date gmtModify;

    /**
     * 业务库
     */
    @JsonProperty("bizDatabase")
    private String bizDatabase;

    @JsonProperty("dbName")
    private String dbName;

    @JsonProperty("shadowDataSource")
    private String shadowDataSource;



    public String getFilterStr(){
        return this.getDataSource()+"@@"+this.getTableUser()+"@@"+this.getType();
    }

}
