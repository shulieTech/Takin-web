package io.shulie.takin.adapter.api.model.request.scenetask;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneTaskUpdateTpsReq extends ContextExt {

    @NotNull
    @ApiModelProperty(value = "场景主键")
    private Long sceneId;

    @NotNull
    @ApiModelProperty(value = "报告主键")
    private Long reportId;

    @NotNull
    @ApiModelProperty(value = "TPS值")
    private Long tpsNum;

    @NotNull
    @ApiModelProperty(value = "节点MD5")
    private String xpathMd5;
}
