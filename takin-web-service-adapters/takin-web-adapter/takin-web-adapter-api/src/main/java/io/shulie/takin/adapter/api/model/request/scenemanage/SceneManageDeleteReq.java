package io.shulie.takin.adapter.api.model.request.scenemanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/10/22 8:06 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageDeleteReq extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "ID")
    private Long id;
}
