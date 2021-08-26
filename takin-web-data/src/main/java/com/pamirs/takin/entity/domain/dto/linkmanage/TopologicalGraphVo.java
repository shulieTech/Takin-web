package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 链路拓扑图
 *
 * @author fanxx
 * @date 2020/7/6 下午4:05
 */
@Data
@ApiModel(value = "TopologicalGraphVo", description = "链路拓扑图节点关系")
public class TopologicalGraphVo {
    @ApiModelProperty(name = "graphNodes", value = "节点信息列表")
    private List<TopologicalGraphNode> graphNodes = new ArrayList<>();
    @ApiModelProperty(name = "graphRelations", value = "节点关系列表")
    private List<TopologicalGraphRelation> graphRelations = new ArrayList<>();
}
