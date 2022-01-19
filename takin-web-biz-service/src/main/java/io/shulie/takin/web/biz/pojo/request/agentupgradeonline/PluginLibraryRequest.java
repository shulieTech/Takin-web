package io.shulie.takin.web.biz.pojo.request.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 插件版本库(PluginLibrary)controller 入参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:24:04
 */
@ApiModel("入参类-入参")
@Data
public class PluginLibraryRequest {

    @ApiModelProperty("id")
    private Long id;

}
