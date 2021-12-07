package io.shulie.takin.web.biz.pojo.response.scenemanage;

import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageListResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "takin-web:列表查询出参")
public class SceneManageListResponse extends SceneManageListResp {
    @ApiModelProperty(value = "位点是否启用 true=启用 false=不启用")
    private Boolean scenePositionEnable;
}
