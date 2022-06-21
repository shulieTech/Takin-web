package io.shulie.takin.adapter.api.model.response.scenetask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * -
 *
 * @author chenhongqiao@shulie.com
 * @date 2021/6/23   下午5:22
 */
@Data
@ApiModel("压测任务状态返回值")
public class SceneJobStateResp {
    /**
     * 状态 未运行：none 运行中：running 运行失败：failed
     */
    @ApiModelProperty(value = "状态值")
    private String state;

    @ApiModelProperty(value = "描述信息")
    private String msg;
}
