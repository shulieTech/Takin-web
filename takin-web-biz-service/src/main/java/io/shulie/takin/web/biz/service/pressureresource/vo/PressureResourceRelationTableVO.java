package io.shulie.takin.web.biz.service.pressureresource.vo;

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
@ToString(callSuper = true)
public class PressureResourceRelationTableVO extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    @ApiModelProperty("数据源Id")
    @Column(name = "`dsId`")
    private Long ds_id;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("业务表")
    private String businessTable;

    @ApiModelProperty("影子表")
    private String shadowTable;

    @ApiModelProperty("是否加入")
    @Column(name = "`join`")
    private int join;

    @ApiModelProperty("扩展信息")
    private int extInfo;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
