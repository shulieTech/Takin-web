package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/25 21:36
 */
@Data
@ApiModel(value = "updatevo", description = "更新的入参数")
public class UpdateVo {
    @ApiModelProperty(name = "id", value = "主键")
    private String id;
}
