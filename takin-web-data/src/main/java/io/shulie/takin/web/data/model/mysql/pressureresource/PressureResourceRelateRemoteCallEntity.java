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
@TableName(value = "t_pressure_resource_relate_remote_call")
@ToString(callSuper = true)
public class PressureResourceRelateRemoteCallEntity extends TenantBaseEntity {
    @Id
    @Column(name = "`id`")
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @Column(name = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("detail_id")
    @Column(name = "`detail_id`")
    private Long detailId;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    @Column(name = "`status`")
    private Integer status;

    @ApiModelProperty("接口名")
    @Column(name = "`interface_name`")
    private String interfaceName;

    @ApiModelProperty("接口类型")
    @Column(name = "`interface_type`")
    private Integer interfaceType;

    @ApiModelProperty("服务端应用名")
    @Column(name = "`server_app_name`")
    private String serverAppName;

    @ApiModelProperty("应用名")
    @Column(name = "`app_name`")
    private String appName;

    @ApiModelProperty("备注")
    @Column(name = "`remark`")
    private String remark;

    @ApiModelProperty("配置类型0:未配置,1:白名单配置,2:返回值mock,3:转发mock")
    @Column(name = "`type`")
    private Integer type;

    @ApiModelProperty("是否放行(0:是 1:否)")
    @Column(name = "`pass`")
    private Integer pass;

    @ApiModelProperty("rpcId")
    @Column(name = "`rpcId`")
    private String rpcId;

    @ApiModelProperty("mock返回值")
    @Column(name = "`mock_return_value`")
    private String mockReturnValue;

    @ApiModelProperty("所属用户")
    @Column(name = "`user_id`")
    private Long userId;

    @ApiModelProperty("是否同步")
    @Column(name = "`is_synchronize`")
    private Integer isSynchronize;

    @ApiModelProperty("接口子类型")
    @Column(name = "`interface_child_type`")
    private String interfaceChildType;

    @ApiModelProperty("是否手动录入 0:否;1:是")
    @Column(name = "`manual_tag`")
    private Integer manualTag;

    @ApiModelProperty("应用名，接口名称，接口类型，租户id,环境code求md5")
    @Column(name = "`md5`")
    private String md5;

    @ApiModelProperty("是否有效 0:有效;1:无效")
    @Column(name = "`is_deleted`")
    private Integer isDeleted;

    @Column(name = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
