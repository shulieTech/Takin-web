package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 插件依赖查询请求
 * @Author ocean_wll
 * @Date 2021/11/11 5:39 下午
 */
@Data
public class PluginDependentQueryRequest {

    @ApiModelProperty(value = "插件名")
    @NotBlank(message = "插件名不能为空")
    private String pluginName;

    @ApiModelProperty(value = "插件版本")
    @NotBlank(message = "插件版本不能为空")
    private String pluginVersion;
}
