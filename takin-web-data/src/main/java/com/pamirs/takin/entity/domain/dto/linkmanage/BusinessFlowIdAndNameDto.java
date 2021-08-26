package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/7 20:39
 */
@Data
@ApiModel(value = "BusinessFlowIdAndNameDto", description = "业务流程名字和id")
public class BusinessFlowIdAndNameDto {
    @ApiModelProperty(name = "id", value = "业务活动id")
    private String id;
    @ApiModelProperty(name = "businessFlowName", value = "业务流程名字")
    private String businessFlowName;
}
