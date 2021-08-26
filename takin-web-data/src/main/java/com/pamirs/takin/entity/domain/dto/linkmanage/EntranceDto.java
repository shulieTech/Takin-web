package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 这个是为了应前端要求,专门匹配的的模型
 *
 * @author vernon
 * @date 2019/12/23 20:00
 */
@Data
@ApiModel(value = "entrancedto", description = "入口查询出参")
public class EntranceDto {
    @ApiModelProperty(name = "label", value = "应用名")
    private String label;
    @ApiModelProperty(name = "label", value = "应用名")
    private String value;
    @ApiModelProperty(name = "entrance", value = "当前应用下的入口集合")
    private List<EntranceDto> children;
}
