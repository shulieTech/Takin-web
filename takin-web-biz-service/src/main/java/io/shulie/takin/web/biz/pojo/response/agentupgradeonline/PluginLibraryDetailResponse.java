package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 插件版本库(PluginLibrary)controller 详情响应类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:24:03
 */
@ApiModel("出参类-详情出参")
@Data
public class PluginLibraryDetailResponse {

    @ApiModelProperty("id")
    private Long id;

}
