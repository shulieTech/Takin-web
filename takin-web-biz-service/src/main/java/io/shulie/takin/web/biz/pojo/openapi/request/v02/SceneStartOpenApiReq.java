package io.shulie.takin.web.biz.pojo.openapi.request.v02;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel
public class SceneStartOpenApiReq{

    @ApiModelProperty(value = "场景ID")
    private Long sceneId;
}
