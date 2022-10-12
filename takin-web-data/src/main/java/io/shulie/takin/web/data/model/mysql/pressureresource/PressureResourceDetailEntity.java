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
@TableName(value = "t_pressure_resource_detail")
@ToString(callSuper = true)
public class PressureResourceDetailEntity extends TenantBaseEntity {
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @TableField(value = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("应用名称")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("入口URL")
    @TableField(value = "`entrance_url`")
    private String entranceUrl;

    @ApiModelProperty("入口名称")
    @TableField(value = "`entrance_name`")
    private String entranceName;

    @ApiModelProperty("请求方式")
    @TableField(value = "`method`")
    private String method;

    @ApiModelProperty("rpcType")
    @TableField(value = "`rpc_type`")
    private String rpcType;

    @ApiModelProperty("extend")
    @TableField(value = "`extend`")
    private String extend;

    @ApiModelProperty("linkId")
    @TableField(value = "`link_id`")
    private String linkId;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @TableField(value = "`type`")
    private Integer type;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
