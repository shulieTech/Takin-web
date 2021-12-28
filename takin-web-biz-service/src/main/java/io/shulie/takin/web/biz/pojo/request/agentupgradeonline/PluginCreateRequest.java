package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/15 10:40 上午
 */
@ApiModel("入参类-发布新探针-插件信息")
@Data
public class PluginCreateRequest {

    @ApiModelProperty("插件名称")
    @NotBlank(message = "插件名称不能为空")
    private String pluginName;

    @ApiModelProperty("插件版本")
    @NotBlank(message = "插件版本不能为空")
    private String pluginVersion;

    @ApiModelProperty("更新内容")
    private String updateInfo;

    // TODO ocean_wll 这里预留租户的配置
    @ApiModelProperty("租户配置")
    private List<Long> tenantIds;
}
