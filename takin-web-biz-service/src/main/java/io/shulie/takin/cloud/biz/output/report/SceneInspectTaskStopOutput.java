package io.shulie.takin.cloud.biz.output.report;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/4/20 8:22 下午
 */
@Data
public class SceneInspectTaskStopOutput {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
