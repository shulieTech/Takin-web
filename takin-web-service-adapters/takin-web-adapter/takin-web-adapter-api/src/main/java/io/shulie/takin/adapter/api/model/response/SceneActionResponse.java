package io.shulie.takin.adapter.api.model.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/11/13 上午11:03
 */
@Data
@ApiModel("状态检查返回值")
public class SceneActionResponse {

    @ApiModelProperty(value = "状态值")
    private Long data;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}