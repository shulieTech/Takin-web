package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 链路拓扑图节点关系
 *
 * @author fanxx
 * @date 2020/7/6 下午4:26
 */
@Data
@ApiModel(value = "TopologicalGraphRelation", description = "链路拓扑图节点关系")
public class TopologicalGraphRelation {
    @ApiModelProperty(name = "from", value = "起始端")
    private String from;
    @ApiModelProperty(name = "to", value = "结束端")
    private String to;
}
