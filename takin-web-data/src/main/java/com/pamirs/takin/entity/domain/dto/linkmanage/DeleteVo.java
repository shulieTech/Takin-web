package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/25 21:28
 */
@Data
@ApiModel(value = "deleteVo", description = "删除的入参数")

public class DeleteVo {
    @ApiModelProperty(name = "id", value = "主键")
    private String id;
}
