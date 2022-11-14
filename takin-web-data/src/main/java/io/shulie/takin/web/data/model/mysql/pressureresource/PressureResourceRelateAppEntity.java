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
@TableName(value = "t_pressure_resource_relate_app")
@ToString(callSuper = true)
public class PressureResourceRelateAppEntity extends TenantBaseEntity {
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @TableField(value = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("配置详情Id")
    @TableField(value = "`detail_id`")
    private Long detailId;

    @ApiModelProperty("应用名称")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("状态(0-正常-1-不正常)")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty("节点数")
    @TableField(value = "`node_num`")
    private Integer nodeNum;

    @ApiModelProperty("是否加入压测范围(0-否 1-是)")
    @TableField(value = "`join_pressure`")
    private Integer joinPressure;

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
