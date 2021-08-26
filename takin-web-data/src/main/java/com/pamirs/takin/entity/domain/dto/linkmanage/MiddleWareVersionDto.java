package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/9 14:26
 */
@Data
@ApiModel(value = "MiddleWareVersionDto", description = "中间件版本")
public class MiddleWareVersionDto {
    @ApiModelProperty(name = "label", value = "中间件版本")
    private String label;
    @ApiModelProperty(name = "value", value = "中间件版本")
    private String value;
}
