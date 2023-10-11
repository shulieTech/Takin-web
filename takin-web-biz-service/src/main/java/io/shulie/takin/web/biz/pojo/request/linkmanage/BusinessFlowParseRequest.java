package io.shulie.takin.web.biz.pojo.request.linkmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "BusinessFlowParseRequest", description = "业务流程解析脚本文件入参")
public class BusinessFlowParseRequest {

    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    private Long scriptDeployId;

    @NotNull(message = "脚本文件 不能为空")
    @ApiModelProperty(name = "scriptFile", value = "脚本文件")
    private FileManageUpdateRequest scriptFile;

    @JsonProperty("pluginConfigs")
    @NotNull(message = "插件信息 不能为空")
    @ApiModelProperty(name = "pluginConfigs", value = "插件信息")
    List<PluginConfigCreateRequest> pluginList;

    // 单接口压测入口
    private Integer source;

}
