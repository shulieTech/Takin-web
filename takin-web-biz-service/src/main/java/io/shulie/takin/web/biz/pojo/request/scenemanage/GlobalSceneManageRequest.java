package io.shulie.takin.web.biz.pojo.request.scenemanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "共享场景参数")
public class GlobalSceneManageRequest {

    @ApiModelProperty("共享场景id")
    private Long id;

    @ApiModelProperty("压测场景id")
    private Long sceneManageId;
}
