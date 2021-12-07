package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 插件依赖库(PluginDependent)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:27:45
 */
@ApiModel("入参类-入参")
@Data
public class PluginDependentRequest {

    @ApiModelProperty("id")
    private Long id;

}
