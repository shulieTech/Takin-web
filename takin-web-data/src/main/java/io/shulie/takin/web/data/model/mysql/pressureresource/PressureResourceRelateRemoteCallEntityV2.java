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
@TableName(value = "t_pressure_resource_relate_remote_call_v2")
@ToString(callSuper = true)
public class PressureResourceRelateRemoteCallEntityV2 extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @TableField(value = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("detail_id")
    @TableField(value = "`detail_id`")
    private Long detailId;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty("接口名")
    @TableField(value = "`interface_name`")
    private String interfaceName;

    @ApiModelProperty("接口类型")
    @TableField(value = "`interface_type`")
    private Integer interfaceType;

    @ApiModelProperty("应用名")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("备注")
    @TableField(value = "`remark`")
    private String remark;

    @ApiModelProperty("是否放行(0:是 1:否)")
    @TableField(value = "`pass`")
    private Integer pass;

    @ApiModelProperty("rpcId")
    @TableField(value = "`rpc_id`")
    private String rpcId;

    @ApiModelProperty("接口子类型")
    @TableField(value = "`interface_child_type`")
    private String interfaceChildType;

    @ApiModelProperty("是否手动录入 0:否;1:是")
    @TableField(value = "`manual_tag`")
    private Integer manualTag;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;

    @ApiModelProperty("应用名，接口名称，接口类型，租户id,环境code求md5")
    @TableField(value = "`md5`")
    private String md5;

    /**
     * 是否找到客户端调用
     */
    @TableField(exist = false)
    private boolean isFind;
}
