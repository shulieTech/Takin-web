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
@TableName(value = "t_pressure_resource_detail")
@ToString(callSuper = true)
public class PressureResourceDetailEntity extends TenantBaseEntity {
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

    @ApiModelProperty("入口URL")
    @Column(name = "`entrance_url`")
    private String entranceUrl;

    @ApiModelProperty("入口名称")
    @Column(name = "`entrance_name`")
    private String entranceName;

    @ApiModelProperty("请求方式")
    @Column(name = "`method`")
    private String method;

    @ApiModelProperty("rpcType")
    @Column(name = "`rpc_type`")
    private String rpcType;

    @ApiModelProperty("extend")
    @Column(name = "`extend`")
    private String extend;

    @ApiModelProperty("linkId")
    @Column(name = "`link_id`")
    private String linkId;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @Column(name = "`type`")
    private int type;

    @Column(name = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
