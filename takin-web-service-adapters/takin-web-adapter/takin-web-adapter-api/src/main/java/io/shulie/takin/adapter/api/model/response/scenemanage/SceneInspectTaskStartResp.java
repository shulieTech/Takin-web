package io.shulie.takin.adapter.api.model.response.scenemanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/4/15 2:45 下午
 */
@Data
@ApiModel("巡检场景任务启动返回值")
public class SceneInspectTaskStartResp {
    @ApiModelProperty(value = "场景ID")
    private Long sceneId;

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
