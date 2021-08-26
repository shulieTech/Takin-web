package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 应用明细
 * @author: CaoYanFei@ShuLie.io
 * @create: 2020-07-19 23:07
 **/
@Data
@ApiModel(value = "applicationDetailDto", description = "应用明细出参")
@Deprecated
public class ApplicationDetailDto implements Serializable {
    @ApiModelProperty(name = "middleWare", value = "中间件列表")
    List<MiddleWareEntity> middleWare;
    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;
    @ApiModelProperty(name = "rpcType", value = "类型")
    private Integer rpcType;
    @ApiModelProperty(name = "rpcTypeTitle", value = "类型名称")
    private String rpcTypeTitle;
    @ApiModelProperty(name = "rpcData", value = "数据")
    private String rpcData;
    @ApiModelProperty(name = "nodes", value = "已知节点列表")
    private Set<String> nodes;
    @ApiModelProperty(name = "unKnowNodes", value = "未知节点列表")
    private Set<String> unKnowNodes;
}
