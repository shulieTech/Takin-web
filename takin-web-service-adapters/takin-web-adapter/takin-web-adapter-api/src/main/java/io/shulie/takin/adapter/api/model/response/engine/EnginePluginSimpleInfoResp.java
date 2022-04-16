package io.shulie.takin.adapter.api.model.response.engine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 引擎插件信息出参
 *
 * @author lipeng
 * @date 2021-01-20 4:56 下午
 */
@Data
@ApiModel("引擎插件文件信息出参")
public class EnginePluginSimpleInfoResp {

    @ApiModelProperty(value = "插件ID", dataType = "long")
    private Long pluginId;

    @ApiModelProperty(value = "插件名称", dataType = "string")
    private String pluginName;

    @ApiModelProperty(value = "插件类型", dataType = "string")
    private String pluginType;

    @ApiModelProperty(value = "更新时间", dataType = "string")
    private String gmtUpdate;

}
