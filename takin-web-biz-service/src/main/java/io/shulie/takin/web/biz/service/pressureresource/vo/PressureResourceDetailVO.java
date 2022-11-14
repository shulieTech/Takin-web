package io.shulie.takin.web.biz.service.pressureresource.vo;

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
public class PressureResourceDetailVO extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("给前端搞个ID的字符串")
    private String value;

    @ApiModelProperty("资源配置Id")
    private String resourceId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("入口URL")
    private String entranceUrl;

    @ApiModelProperty("入口名称")
    private String entranceName;

    @ApiModelProperty("rpcType")
    private String rpcType;

    @ApiModelProperty("extend")
    private String extend;

    @ApiModelProperty("请求方式")
    private String method;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
