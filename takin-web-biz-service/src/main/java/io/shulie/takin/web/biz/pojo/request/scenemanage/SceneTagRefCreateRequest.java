package io.shulie.takin.web.biz.pojo.request.scenemanage;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 15:51
 */

@Data
@ApiModel(value = "场景标签关联模型")
public class SceneTagRefCreateRequest implements Serializable {

    private static final long serialVersionUID = -8628119887637215236L;

    /**
     * 脚本发布实例id
     */

    @JsonProperty("sceneId")
    @NotNull(message = "场景id不能为空")
    @ApiModelProperty(value = "sceneId")
    private Long sceneId;

    /**
     * 标签ids
     */
    @NotNull(message = "标签id不能为空")
    @ApiModelProperty(value = "tagNames")
    private List<String> tagNames;
}
