package io.shulie.takin.web.biz.pojo.request.scenemanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 14:19
 */
@Data
@ApiModel(value = "场景标签创建模型")
public class SceneTagCreateRequest implements Serializable {
    private static final long serialVersionUID = -7849149905765078978L;
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty("标签名称")
    private String tagName;
}
