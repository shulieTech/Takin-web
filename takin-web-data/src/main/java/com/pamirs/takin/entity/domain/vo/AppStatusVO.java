package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-04-07 22:58
 */

@Data
@ApiModel(value = "AppStatusVO", description = "应用异常vo")
public class AppStatusVO implements Serializable {
    private static final long serialVersionUID = 7784138249203384109L;

    @ApiModelProperty(name = "applicationName", value = "项目名称")
    private String applicationName;

    @ApiModelProperty(name = "exceptionInfo", value = "异常信息")
    private String exceptionInfo;

    public AppStatusVO(String applicationName, String exceptionInfo) {
        this.applicationName = applicationName;
        this.exceptionInfo = exceptionInfo;
    }

    public AppStatusVO() {
    }
}
