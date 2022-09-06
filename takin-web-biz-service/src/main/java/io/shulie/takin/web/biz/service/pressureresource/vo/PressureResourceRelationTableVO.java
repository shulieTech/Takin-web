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
    private String id;

    @ApiModelProperty("资源配置Id")
    private String resourceId;

    @ApiModelProperty("数据源Id")
    private String dsId;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    private Integer status;

    @ApiModelProperty("业务表")
    private String businessTable;

    @ApiModelProperty("影子表")
    private String shadowTable;

    @ApiModelProperty("是否加入")
    private int joinFlag;

    @ApiModelProperty("类型(0-手工 1-自动)")
    private int type;

    @ApiModelProperty("扩展信息")
    private int extInfo;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
