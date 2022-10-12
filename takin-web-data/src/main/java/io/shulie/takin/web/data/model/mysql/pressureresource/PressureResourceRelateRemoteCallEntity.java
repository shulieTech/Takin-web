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
@TableName(value = "t_pressure_resource_relate_remote_call")
@ToString(callSuper = true)
public class PressureResourceRelateRemoteCallEntity extends TenantBaseEntity {
    @TableId(value = "ID", type = IdType.AUTO)
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

    @ApiModelProperty("服务端应用名")
    @TableField(value = "`server_app_name`")
    private String serverAppName;

    @ApiModelProperty("应用名")
    @TableField(value = "`app_name`")
    private String appName;

    @ApiModelProperty("备注")
    @TableField(value = "`remark`")
    private String remark;

    @ApiModelProperty("配置类型0:未配置,1:白名单配置,2:返回值mock,3:转发mock")
    @TableField(value = "`type`")
    private Integer type;

    @ApiModelProperty("是否放行(0:是 1:否)")
    @TableField(value = "`pass`")
    private Integer pass;

    @ApiModelProperty("rpcId")
    @TableField(value = "`rpc_id`")
    private String rpcId;

    @ApiModelProperty("mock返回值")
    @TableField(value = "`mock_return_value`")
    private String mockReturnValue;

    @ApiModelProperty("所属用户")
    @TableField(value = "`user_id`")
    private Long userId;

    @ApiModelProperty("是否同步")
    @TableField(value = "`is_synchronize`")
    private Integer isSynchronize;

    @ApiModelProperty("接口子类型")
    @TableField(value = "`interface_child_type`")
    private String interfaceChildType;

    @ApiModelProperty("是否手动录入 0:否;1:是")
    @TableField(value = "`manual_tag`")
    private Integer manualTag;

    @ApiModelProperty("应用名，接口名称，接口类型，租户id,环境code求md5")
    @TableField(value = "`md5`")
    private String md5;

    @ApiModelProperty("是否有效 0:有效;1:无效")
    @TableField(value = "`is_deleted`")
    private Integer isDeleted;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
