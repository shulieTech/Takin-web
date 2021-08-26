package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/24 16:15
 */
@Data
@ApiModel(value = "SystemProcessIdAndNameDto", description = "系统流程名字和id")
public class SystemProcessIdAndNameDto {
    @ApiModelProperty(name = "id", value = "系统流程id")
    private String id;
    @ApiModelProperty(name = "systemProcessName", value = "系统流程名字")
    private String systemProcessName;
}
