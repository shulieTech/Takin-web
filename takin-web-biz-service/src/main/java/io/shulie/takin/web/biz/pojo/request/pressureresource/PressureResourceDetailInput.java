package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class PressureResourceDetailInput {
    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("入口URL")
    private String entranceUrl;

    @ApiModelProperty("入口名称")
    private String entranceName;

    @ApiModelProperty("类型-是否虚拟")
    private int type;

    @ApiModelProperty("请求方式")
    private String method;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}