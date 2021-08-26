package io.shulie.takin.web.biz.pojo.request.scenemanage;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-03 16:55
 */

@Data
@ApiModel(value = "取消场景定时启动模型")
public class SceneSchedulerDeleteRequest {
    @ApiModelProperty(name = "sceneId", value = "场景id")
    @NotNull(message = "场景id不能为空")
    private Long sceneId;
}
