package io.shulie.takin.cloud.biz.input.scenemanage;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneTaskQueryTpsInput extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "sceneId")
    private Long sceneId;

    @NotNull
    @ApiModelProperty(value = "reportId")
    private Long reportId;

    @NotNull
    @ApiModelProperty(value = "节点MD5")
    private String xpathMd5;

}
