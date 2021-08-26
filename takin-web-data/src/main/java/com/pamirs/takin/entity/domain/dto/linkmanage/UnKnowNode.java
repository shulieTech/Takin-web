package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "TechLinkDto.UnKnowNode", description = "技术链路出参-未知节点")
@Data
public class UnKnowNode implements Serializable {
    @ApiModelProperty(name = "rpcUrl", value = "请求地址")
    String rpcUrl;
    @ApiModelProperty(name = "rpcType", value = "请求类型")
    Integer rpcType;
    @ApiModelProperty(name = "rpcTypeTitle", value = "请求类型标题")
    String rpcTypeTitle;
    @ApiModelProperty(name = "rpcRequestParam", value = "请求参数")
    String rpcRequestParam;
    @ApiModelProperty(name = "rpcSrc", value = "请求来源")
    String rpcSrc;
}
