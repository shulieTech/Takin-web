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
@TableName(value = "t_pressure_resource_relate_app")
@ToString(callSuper = true)
public class PressureResourceRelateAppEntity extends TenantBaseEntity {
    @Id
    @Column(name = "`id`")
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @Column(name = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("配置详情Id")
    @Column(name = "`detail_id`")
    private Long detailId;

    @ApiModelProperty("应用名称")
    @Column(name = "`app_name`")
    private String appName;

    @ApiModelProperty("状态(0-正常-1-不正常)")
    @Column(name = "`status`")
    private Integer status;

    @ApiModelProperty("节点数")
    @Column(name = "`node_num`")
    private Integer nodeNum;

    @ApiModelProperty("是否加入压测范围(0-否 1-是)")
    @Column(name = "`join_pressure`")
    private Integer joinPressure;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @Column(name = "`type`")
    private Integer type;

    @Column(name = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
