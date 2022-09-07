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
public class PressureResourceRelateAppVO extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("资源配置Id")
    private String resourceId;

    @ApiModelProperty("详情Id")
    private String detailId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("状态 0-正常 1-不正常")
    @Column(name = "`status`")
    private int status;

    @ApiModelProperty("隔离方式(0-无 1-影子库 2-影子库/影子表 3-影子表)")
    private int isolateType;

    @ApiModelProperty("节点数")
    private Integer nodeNum;

    @ApiModelProperty("探针节点数")
    private Integer agentNodeNum;

    @ApiModelProperty("是否加入压测范围(0-否 1-是)")
    private int joinPressure;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
