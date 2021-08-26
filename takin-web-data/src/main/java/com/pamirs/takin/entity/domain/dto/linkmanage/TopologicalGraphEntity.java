package com.pamirs.takin.entity.domain.dto.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/7/7 下午8:23
 */
@Data
@ApiModel(value = "TopologicalGraphEntity", description = "链路变更前后拓扑图")
public class TopologicalGraphEntity {
    @ApiModelProperty(name = "topologicalGraphBeforeVo", value = "链路变更前拓扑图")
    private TopologicalGraphVo topologicalGraphBeforeVo;
    @ApiModelProperty(name = "topologicalGraphAfterVo", value = "链路变更后拓扑图")
    private TopologicalGraphVo topologicalGraphAfterVo;
}
