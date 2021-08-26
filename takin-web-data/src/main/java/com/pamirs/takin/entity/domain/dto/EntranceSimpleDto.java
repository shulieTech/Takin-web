package com.pamirs.takin.entity.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.2.0入口模型
 *
 * @author  fanxx
 * @date 2020/07/02 20:00
 */
@Data
@ApiModel(value = "entrancedto", description = "入口查询出参")
public class EntranceSimpleDto {
    @ApiModelProperty(name = "label", value = "应用名")
    private String label;
    @ApiModelProperty(name = "label", value = "应用名")
    private String value;
}
