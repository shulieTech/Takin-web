package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/9 14:24
 */
@ApiModel(value = "MiddleWareNameDto", description = "中间件名字")
@Data
public class MiddleWareNameDto {

    @ApiModelProperty(name = "label", value = "中间件名字")
    private String label;
    @ApiModelProperty(name = "value", value = "中间件名字")
    private String value;
    @ApiModelProperty(name = "children", value = "下属的中间版本")
    private List<MiddleWareVersionDto> children;
}
