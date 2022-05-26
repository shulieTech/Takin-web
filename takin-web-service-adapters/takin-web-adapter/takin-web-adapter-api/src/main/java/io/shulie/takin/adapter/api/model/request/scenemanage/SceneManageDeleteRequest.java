package io.shulie.takin.adapter.api.model.request.scenemanage;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/21 下午5:01
 */
@Data
public class SceneManageDeleteRequest {

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;
}
