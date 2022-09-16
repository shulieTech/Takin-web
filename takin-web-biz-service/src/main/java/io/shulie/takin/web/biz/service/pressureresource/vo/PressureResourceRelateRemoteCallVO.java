package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
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
@ToString(callSuper = true)
public class PressureResourceRelateRemoteCallVO extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("资源配置Id")
    private String resourceId;

    @ApiModelProperty("detail_id")
    private String detailId;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    private Integer status;

    @ApiModelProperty("接口名")
    private String interfaceName;

    @ApiModelProperty("接口类型")
    private Integer interfaceType;

    @ApiModelProperty("服务端应用名")
    private String serverAppName;

    @ApiModelProperty("应用名")
    private String appName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("配置类型0:未配置,1:白名单配置,2:返回值mock,3:转发mock")
    private Integer type;

    @ApiModelProperty("是否放行(0:是 1:否)")
    private Integer pass;

    @ApiModelProperty("mock返回值")
    private String mockReturnValue;

    @ApiModelProperty("所属用户")
    private Long userId;

    @ApiModelProperty("是否同步")
    private Integer isSynchronize;

    @ApiModelProperty("接口子类型")
    private String interfaceChildType;

    @ApiModelProperty("是否手动录入 0:否;1:是")
    private Integer manualTag;

    @ApiModelProperty("应用名，接口名称，接口类型，租户id,环境code求md5")
    private String md5;

    @ApiModelProperty("是否有效 0:有效;1:无效")
    private Integer isDeleted;

    @Column(name = "`gmt_create`")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    private Date gmtModified;

    /**
     * 调用依赖
     */
    private String invoke;

    @ApiModelProperty("mock数据")
    private MockInfo mockInfo;
}
