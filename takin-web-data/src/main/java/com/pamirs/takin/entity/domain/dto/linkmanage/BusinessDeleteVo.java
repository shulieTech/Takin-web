package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/7 18:57
 */
@Data
public class BusinessDeleteVo {
    @ApiModelProperty(name = "linkId", value = "主键")
    private String linkId;
}
