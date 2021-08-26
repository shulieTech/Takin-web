package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/6 11:14
 */
@Data
@ApiModel(value = "ExistBusinessActiveDto", description = "已经存在的业务活动对象")
public class ExistBusinessActiveDto {
    @ApiModelProperty(name = "key", value = "前端的uuid")
    private String key;
    @ApiModelProperty(name = "id", value = "业务活动Id")
    private String id;
}
