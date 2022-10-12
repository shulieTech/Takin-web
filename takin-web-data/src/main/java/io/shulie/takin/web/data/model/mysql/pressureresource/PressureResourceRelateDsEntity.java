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
@TableName(value = "t_pressure_resource_relate_ds")
@ToString(callSuper = true)
public class PressureResourceRelateDsEntity extends TenantBaseEntity {
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @TableField(value = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("链路详情Id")
    @TableField(value = "`detail_id`")
    private Long detailId;

    @ApiModelProperty("应用名称")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("中间件名称 druid, hikari,c3p0等")
    @TableField(value = "`middleware_name`")
    private String middlewareName;

    @ApiModelProperty("中间件类型 缓存/连接池")
    @TableField(value = "`middleware_type`")
    private String middlewareType;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @TableField(value = "`type`")
    private Integer type;

    @ApiModelProperty("唯一键")
    @TableField(value = "`unique_key`")
    private String uniqueKey;

    @ApiModelProperty("业务数据源")
    @TableField(value = "`business_database`")
    private String businessDatabase;

    @ApiModelProperty("业务数据源用户名")
    @TableField(value = "`business_user_name`")
    private String businessUserName;

    @ApiModelProperty("影子数据源")
    @TableField(value = "`shadow_database`")
    private String shadowDatabase;

    @ApiModelProperty("影子数据源用户名")
    @TableField(value = "`shadow_user_name`")
    private String shadowUserName;

    @ApiModelProperty("影子数据源密码")
    @TableField(value = "`shadow_password`")
    private String shadowPassword;

    @ApiModelProperty("扩展信息")
    @TableField(value = "`ext_info`")
    private String extInfo;

    @ApiModelProperty("remark")
    @TableField(value = "`remark`")
    private String remark;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
