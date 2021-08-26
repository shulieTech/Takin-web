package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/26 17:03
 */
@Data
@ApiModel(value = "BusinessActiveIdAndNameDto", description = "业务活动款名字和id")
public class BusinessActiveIdAndNameDto {

    @ApiModelProperty(name = "id", value = "业务活动id")
    private String id;
    @ApiModelProperty(name = "businessActiveName", value = "业务活动名字")
    private String businessActiveName;
}
