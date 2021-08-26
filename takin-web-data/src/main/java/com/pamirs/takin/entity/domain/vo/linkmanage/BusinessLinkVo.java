package com.pamirs.takin.entity.domain.vo.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/11/30 16:11
 */
@Data
@ApiModel(value = "BusinessLinkVo", description = "业务活动入参")
public class BusinessLinkVo {
    @ApiModelProperty(name = "linkid", value = "业务活动id")
    private String id;

    @ApiModelProperty(name = "linkName", value = "业务活动名字")
    private String linkName;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    private String link_level;

    @ApiModelProperty(name = "parent_business_id", value = "业务活动的上级业务活动")
    private String parent_business_id;

    @ApiModelProperty(name = "isCore", value = "业务活动链路是否核心链路 0:不是;1:是")
    private Integer isCore;
    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;
    @ApiModelProperty(name = "relatedTechLinkId", value = "关联的系统流程id")
    private String relatedTechLinkId;

}
