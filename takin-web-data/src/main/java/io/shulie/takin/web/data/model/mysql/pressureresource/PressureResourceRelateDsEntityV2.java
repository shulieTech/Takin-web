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
@TableName(value = "t_pressure_resource_relate_ds_v2")
@ToString(callSuper = true)
public class PressureResourceRelateDsEntityV2 extends TenantBaseEntity {
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @TableField(value = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("应用名称")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("链路详情Id")
    @TableField(value = "`detail_id`")
    private Long detailId;

    @ApiModelProperty("业务数据源")
    @TableField(value = "`business_database`")
    private String businessDatabase;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @TableField(value = "`type`")
    private Integer type;

    @ApiModelProperty("remark")
    @TableField(value = "`remark`")
    private String remark;

    @ApiModelProperty("关联数据源Id")
    @TableField(value = "`relate_id`")
    private Long relateId;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
