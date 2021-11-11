package io.shulie.takin.web.biz.pojo.request.linkmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "BusinessFlowUpdateRequest", description = "业务流程更新入参")
public class BusinessFlowUpdateRequest implements Serializable {

    @NotNull(message = "业务流程id不能为空")
    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @ApiModelProperty(name = "sceneName", value = "业务流程名字")
    private String sceneName;

    @ApiModelProperty(name = "isCore", value = "是否核心")
    private Integer isCore;

    @ApiModelProperty(name = "sceneLevel", value = "场景等级")
    private String sceneLevel;

}
