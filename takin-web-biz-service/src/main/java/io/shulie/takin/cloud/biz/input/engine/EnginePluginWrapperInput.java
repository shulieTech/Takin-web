package io.shulie.takin.cloud.biz.input.engine;

import java.util.List;

import io.shulie.takin.cloud.common.bean.file.FileManageInfo;
import lombok.Data;

/**
 * 引擎插件保存入参
 *
 * @author lipeng
 * @date 2021-01-12 11:05 上午
 */
@Data
public class EnginePluginWrapperInput {

    private Long pluginId;

    private String pluginName;

    private String pluginType;

    private String pluginUploadPath;

    private List<String> supportedVersions;

    private List<FileManageInfo> uploadFiles;

}
