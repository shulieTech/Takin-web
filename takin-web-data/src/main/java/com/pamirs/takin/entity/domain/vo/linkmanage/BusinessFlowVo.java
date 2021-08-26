package com.pamirs.takin.entity.domain.vo.linkmanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/3 22:54
 */
@Data
@ApiModel(value = "BusinessFlowVo", description = "业务流程入参树")
public class BusinessFlowVo {
    @ApiModelProperty(name = "id", value = "主键")
    private String id;
    @ApiModelProperty(name = "sceneName", value = "业务流程名字")
    private String sceneName;
    @ApiModelProperty(name = "isCore", value = "是否核心")
    private String isCore;
    @ApiModelProperty(name = "sceneLevel", value = "场景等级")
    private String sceneLevel;
    @ApiModelProperty(name = "root", value = "根节点")
    private List<BusinessFlowTree> root;
}
