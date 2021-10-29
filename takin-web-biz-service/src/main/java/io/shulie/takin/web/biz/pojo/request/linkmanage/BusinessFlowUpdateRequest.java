package io.shulie.takin.web.biz.pojo.request.linkmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigUpdateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "BusinessFlowUpdateRequest", description = "业务流程更新入参")
public class BusinessFlowUpdateRequest {

    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @ApiModelProperty(name = "scriptFile", value = "脚本文件")
    private FileManageUpdateRequest scriptFile;

    /**
     * 关联文件列表
     */
    @JsonProperty("uploadFiles")
    private List<FileManageUpdateRequest> fileManageUpdateRequests;

    /**
     * 关联附件列表
     */
    @JsonProperty("uploadAttachments")
    private List<FileManageUpdateRequest> attachmentManageUpdateRequests;

    /**
     * 引擎插件列表
     */
    @JsonProperty("pluginConfigs")
    private List<PluginConfigUpdateRequest> pluginConfigUpdateRequests;

}
