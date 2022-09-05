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
@TableName(value = "t_pressure_resource_relation_ds")
@ToString(callSuper = true)
public class PressureResourceRelationDsEntity extends TenantBaseEntity {
    @Id
    @Column(name = "`id`")
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @Column(name = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("应用名称")
    @Column(name = "`app_name`")
    private String appName;

    @ApiModelProperty("中间件名称")
    @Column(name = "`middleware_name`")
    private String middlewareName;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    @Column(name = "`status`")
    private String status;

    @ApiModelProperty("业务数据源")
    @Column(name = "`business_database`")
    private String businessDatabase;

    @ApiModelProperty("业务数据源用户名")
    @Column(name = "`business_user_name`")
    private String businessUserName;

    @ApiModelProperty("影子数据源")
    @Column(name = "`shadow_database`")
    private String shadowDatabase;

    @ApiModelProperty("影子数据源用户名")
    @Column(name = "`shadow_user_name`")
    private String shadowUserName;

    @ApiModelProperty("影子数据源密码")
    @Column(name = "`shadow_password`")
    private String shadowPassword;

    @ApiModelProperty("扩展信息")
    @Column(name = "`ext_info`")
    private String extInfo;

    @ApiModelProperty("remark")
    @Column(name = "`remark`")
    private String remark;

    @Column(name = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
