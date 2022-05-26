package io.shulie.takin.adapter.api.model.response.engine;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 引擎插件详情出参
 *
 * @author lipeng
 * @date 2021-01-20 4:26 下午
 */
@Data
@ApiModel("引擎插件详情出参")
public class EnginePluginDetailResp {

    @ApiModelProperty("插件ID")
    private Long pluginId;

    @ApiModelProperty("插件类型")
    private String pluginType;

    @ApiModelProperty("插件名称")
    private String pluginName;

    @ApiModelProperty("支持的版本号")
    private List<String> supportedVersions;

    @ApiModelProperty("上传的文件信息")
    private List<EnginePluginFileResp> uploadFiles;

}
