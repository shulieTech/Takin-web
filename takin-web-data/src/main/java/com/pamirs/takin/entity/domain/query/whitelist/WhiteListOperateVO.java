package com.pamirs.takin.entity.domain.query.whitelist;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "WhiteListOperateVO", description = "（批量）加入/取消白名单接口入参")
public class WhiteListOperateVO {

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    @NotNull
    private Long applicationId;

    @ApiModelProperty(name = "ids", value = "白名单ID列表")
    @NotEmpty
    private List<String> ids;

    @ApiModelProperty(name = "type", value = "操作类型:移出白名单:0 加入白名单:1")
    private Integer type;

}
