package io.shulie.takin.web.data.model.mysql.pressureresource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("应用名称")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("业务数据源")
    @TableField(value = "`data_source`")
    private String dataSource;

    @ApiModelProperty("影子数据源")
    @TableField(value = "`shadow_data_source`")
    private String shadowDataSource;

    @ApiModelProperty("数据库类型名称")
    @TableField(value = "`db_name`")
    private String dbName;

    @ApiModelProperty("用户名")
    @TableField(value = "`table_user`")
    private String tableUser;

    @ApiModelProperty("用户密码")
    @TableField(value = "`password`")
    private String password;

    @ApiModelProperty("中间件类型")
    @TableField(value = "`middleware_type`")
    private String middlewareType;

    @ApiModelProperty("连接池名称")
    @TableField(value = "`connection_pool`")
    private String connectionPool;

    @ApiModelProperty("附加信息")
    @TableField(value = "`ext_info`")
    private String extInfo;

    @ApiModelProperty("类型")
    @TableField(value = "`type`")
    private String type;

    @ApiModelProperty("动态配置")
    @TableField(value = "`attachment`")
    private String attachment;

    @ApiModelProperty("唯一健(md5(app_name,data_source,table_user))")
    @TableField(value = "`uniqueKey`")
    private String uniqueKey;

    @TableField(value = "user_app_key")
    private String userAppKey;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
