package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/4/20 8:27 下午
 */
@Data
public class SceneInspectTaskStopResp {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
