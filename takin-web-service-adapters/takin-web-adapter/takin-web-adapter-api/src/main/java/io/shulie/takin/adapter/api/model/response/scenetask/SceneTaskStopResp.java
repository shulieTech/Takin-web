package io.shulie.takin.adapter.api.model.response.scenetask;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liyuanba
 * @date 2021/11/26 2:18 下午
 */
@Data
public class SceneTaskStopResp {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "任务ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msgs = new ArrayList<>(0);
}
