package com.pamirs.takin.entity.domain.query.whitelist;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "WhiteListCreateListVO", description = "新增白名单入参")
public class WhiteListCreateListVO {

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    @NotNull(message = "接口类型不能为空")
    private Integer interfaceType;

    @ApiModelProperty(name = "interfaceList", value = "接口列表")
    @NotNull(message = "接口列表不能为空")
    private List<String> interfaceList;

}
