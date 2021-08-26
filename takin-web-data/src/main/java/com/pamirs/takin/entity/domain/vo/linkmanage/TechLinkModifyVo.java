package com.pamirs.takin.entity.domain.vo.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/12/26 17:19
 */
@Data
@ApiModel(value = "TechLinkModifyVo", description = "技术链路修改入参")
public class TechLinkModifyVo {
    @ApiModelProperty(name = "linkId", value = "系统流程id")
    private String linkId;
    @ApiModelProperty(name = "entrance", value = "入口")
    private String entrance;
    @ApiModelProperty(name = "linkName", value = "链路名")
    private String linkName;
}
