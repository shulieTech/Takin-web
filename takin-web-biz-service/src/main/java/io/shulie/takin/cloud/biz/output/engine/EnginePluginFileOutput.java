package io.shulie.takin.cloud.biz.output.engine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 引擎插件文件信息出参
 *
 * @author lipeng
 * @date 2021-01-14 3:53 下午
 */
@Data
@ApiModel("引擎插件文件信息出参")
public class EnginePluginFileOutput {

    @ApiModelProperty("文件ID")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("是否删除")
    private Boolean isDeleted;
}
