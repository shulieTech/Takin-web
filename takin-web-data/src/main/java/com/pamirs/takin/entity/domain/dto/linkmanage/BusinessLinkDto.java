package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 业务链路出参
 *
 * @author vernon
 * @date 2019/11/30 16:05
 */
@Data
@Deprecated
@ApiModel(value = "businessLinkDto", description = "业务链路出参")
public class BusinessLinkDto implements Serializable {

    @ApiModelProperty(name = "id", value = "业务活动id")
    private String id;
    @ApiModelProperty(name = "linkName", value = "业务活动名字")
    private String linkName;
    @ApiModelProperty(name = "entrance", value = "入口")
    private String entrance;
    @ApiModelProperty(name = "ischange", value = "是否变更")
    private String ischange;
    @ApiModelProperty(name = "createTime", value = "创建时间")
    private java.util.Date createTime;
    @ApiModelProperty(name = "updateTime", value = "修改时间")
    private java.util.Date updateTime;
    @ApiModelProperty(name = "techLinkDto", value = "关联的技术链路")
    private TechLinkDto techLinkDto;
    @ApiModelProperty(name = "candelete", value = "是否可以删除")
    private String candelete;
    @ApiModelProperty(name = "isCore", value = "是否否核心链路 0:不是;1:是'")
    private String isCore;
    @ApiModelProperty(name = "linkLevel", value = "链路等级")
    private String linkLevel;
    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;
    @ApiModelProperty(name = "child", value = "子业务活动,针对于业务流程详情的出参")
    private BusinessLinkDto child;
    @ApiModelProperty(name = "userId", value = "用户id")
    private Long userId;
}
