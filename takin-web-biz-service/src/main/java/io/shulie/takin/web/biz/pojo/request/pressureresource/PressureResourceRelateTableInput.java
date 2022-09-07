package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.surge.data.common.doc.annotation.Id;
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
@ToString(callSuper = true)
public class PressureResourceRelateTableInput extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    @ApiModelProperty("数据源配置Id")
    private String dsId;

    @ApiModelProperty("数据源")
    private String database;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("业务表")
    private String businessTable;

    @ApiModelProperty("影子表")
    private String shadowTable;

    @ApiModelProperty("是否加入(0-加入 1-未加入)")
    private Integer joinFlag;

    @ApiModelProperty("扩展信息")
    private String extInfo;

    @ApiModelProperty("类型(0-手工 1-自动)")
    private Integer type;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
