package io.shulie.takin.web.biz.pojo.request.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "BusinessFlowAutoMatchRequest", description = "自动匹配入参")
public class BusinessFlowAutoMatchRequest {

    @NotNull
    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;
}
