package com.pamirs.takin.entity.domain.query.whitelist;

import javax.validation.constraints.NotNull;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ApiModel(value = "WhiteListQueryVO", description = "白名单列表接口入参")
public class WhiteListQueryVO extends PagingDevice {

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    @NotNull
    private Long applicationId;

    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    private Integer interfaceType;

    @ApiModelProperty(name = "interfaceName", value = "接口名称")
    private String interfaceName;

    @ApiModelProperty(name = "useYn", value = "是否已加入")
    private Integer useYn;

}
