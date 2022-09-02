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
@TableName(value = "t_pressure_resource_relation_table")
@ToString(callSuper = true)
public class PressureResourceRelationTableEntity extends TenantBaseEntity {
    @Id
    @Column(name = "`id`")
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    @Column(name = "`resource_id`")
    private Long resourceId;

    @ApiModelProperty("数据源Id")
    @Column(name = "`ds_id`")
    private Long dsId;

    @ApiModelProperty("状态(0-正常 1-不正常)")
    @Column(name = "`status`")
    private String status;

    @ApiModelProperty("业务表")
    @Column(name = "`business_table`")
    private String businessTable;

    @ApiModelProperty("影子表")
    @Column(name = "`shadow_table`")
    private String shadowTable;

    @ApiModelProperty("是否加入(0-加入 1-未加入)")
    @Column(name = "`join`")
    private int join;

    @ApiModelProperty("扩展信息")
    @Column(name = "`ext_info`")
    private String extInfo;

    @ApiModelProperty("备注")
    @Column(name = "`remark`")
    private String remark;

    @Column(name = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @Column(name = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
