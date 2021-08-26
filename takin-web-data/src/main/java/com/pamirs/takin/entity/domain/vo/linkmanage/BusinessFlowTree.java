package com.pamirs.takin.entity.domain.vo.linkmanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/3 22:51
 */
@Data
@ApiModel(value = "BusinessFlowTree", description = "业务流程树")
public class BusinessFlowTree {
    @ApiModelProperty(name = "children", value = "子节点")
    List<BusinessFlowTree> children;
    @ApiModelProperty(name = "key", value = "唯一标识")
    private String key;
    @ApiModelProperty(name = "id", value = "主键")
    private String id;
}
