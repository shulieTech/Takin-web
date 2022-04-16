package io.shulie.takin.cloud.biz.output.engine;

import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.EnginePluginEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 引擎插件详情
 *
 * @author lipeng
 * @date 2021-01-14 3:48 下午
 */
@Data
@ApiModel("引擎插件详情出参")
public class EnginePluginDetailOutput {

    @ApiModelProperty("插件ID")
    private Long pluginId;

    @ApiModelProperty("插件类型")
    private String pluginType;

    @ApiModelProperty("插件名称")
    private String pluginName;

    @ApiModelProperty("支持的版本号")
    private List<String> supportedVersions;

    @ApiModelProperty("上传的文件信息")
    private List<EnginePluginFileOutput> uploadFiles;

    /**
     * 创建实例
     *
     * @param enginePlugin
     * @param supportedVersions
     * @param uploadFiles
     * @return -
     */
    public static EnginePluginDetailOutput create(EnginePluginEntity enginePlugin, List<String> supportedVersions, List<EnginePluginFileOutput> uploadFiles) {
        EnginePluginDetailOutput instance = new EnginePluginDetailOutput();
        instance.setPluginId(enginePlugin.getId());
        instance.setPluginName(enginePlugin.getPluginName());
        instance.setPluginType(enginePlugin.getPluginType());
        instance.setSupportedVersions(supportedVersions);
        instance.setUploadFiles(uploadFiles);
        return instance;
    }
}