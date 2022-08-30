package io.shulie.takin.web.data.param.application;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 9:41 AM
 */
@ApiModel(value = "AppDatabaseInputParam", description = "agent上报应用数据源信息")
@Data
public class AppDatabaseInputParam {
    @ApiModelProperty(name = "appName", value = "应用名称", required = true)
    private String appName;

    @ApiModelProperty(name = "dataSource", value = "业务数据源", required = false)
    private String dataSource;

    @ApiModelProperty(name = "shadow_data_source", value = "影子数据源", required = false)
    private String shadowDataSource;

    @ApiModelProperty(name = "dbName", value = "数据库类型", required = true)
    private String dbName;

    @ApiModelProperty(name = "tableUser", value = "用户名称", required = true)
    private String tableUser;

    @ApiModelProperty(name = "password", value = "密码", required = true)
    private String password;

    @ApiModelProperty(name = "middlewareType", value = "中间件类型", required = true)
    private String middlewareType;

    @ApiModelProperty(name = "connectionPool", value = "连接池类型(durid等)", required = true)
    private String connectionPool;

    @ApiModelProperty(name = "type", value = "type,来源", required = false)
    private String type;

    @ApiModelProperty(name = "extInfo", value = "附加信息", required = false)
    private String extInfo;

    @ApiModelProperty(name = "attachment", value = "附件", required = false)
    private String attachment;

    @ApiModelProperty(name = "userAppKey", value = "userAppKey", required = true)
    private String userAppKey;

    @ApiModelProperty(name = "envCode", value = "envCode", required = true)
    private String envCode;

    @ApiModelProperty(name = "tenantId", value = "tenantId", required = false)
    private String tenantId;
}
