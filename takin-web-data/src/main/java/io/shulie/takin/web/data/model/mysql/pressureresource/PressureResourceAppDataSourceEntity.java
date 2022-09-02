package io.shulie.takin.web.data.model.mysql.pressureresource;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.influxdb.annotation.Column;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@TableName(value = "t_pressure_resource_app_database")
@ToString(callSuper = true)
public class PressureResourceAppDataSourceEntity extends TenantBaseEntity {
    @Id
    @Column(name = "`id`")
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("应用名称")
    @Column(name = "`app_name`")
    private String appName;

    @ApiModelProperty("业务数据源")
    @Column(name = "`data_source`")
    private String dataSource;

    @ApiModelProperty("影子数据源")
    @Column(name = "`shadow_data_source`")
    private String shadowDataSource;

    @ApiModelProperty("数据库类型名称")
    @Column(name = "`db_name`")
    private String dbName;

    @ApiModelProperty("用户名")
    @Column(name = "`table_user`")
    private String tableUser;

    @ApiModelProperty("用户密码")
    @Column(name = "`password`")
    private String password;

    @ApiModelProperty("中间件类型")
    @Column(name = "`middleware_type`")
    private String middlewareType;

    @ApiModelProperty("连接池名称")
    @Column(name = "`connection_pool`")
    private String connectionPool;

    @ApiModelProperty("附加信息")
    @Column(name = "`ext_info`")
    private String extInfo;

    @ApiModelProperty("类型")
    @Column(name = "`type`")
    private String type;

    @ApiModelProperty("动态配置")
    @Column(name = "`attachment`")
    private String attachment;

    @ApiModelProperty("唯一健(md5(app_name,data_source,table_user))")
    @Column(name = "`uniqueKey`")
    private String uniqueKey;

    @Column(name = "user_app_key")
    private String userAppKey;

    @Column(name = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
