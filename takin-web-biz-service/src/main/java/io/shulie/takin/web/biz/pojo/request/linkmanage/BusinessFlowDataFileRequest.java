package io.shulie.takin.web.biz.pojo.request.linkmanage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigUpdateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "BusinessFlowDataFileRequest", description = "业务流程数据文件入参")
public class BusinessFlowDataFileRequest implements Serializable {

    @NotNull
    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @ApiModelProperty(name = "uploadFiles", value = "数据文件")
    @JsonProperty("uploadFiles")
    private List<FileManageUpdateRequest> fileManageUpdateRequests;

    @JsonProperty("uploadAttachments")
    @ApiModelProperty(name = "uploadAttachments", value = "关联附件列表")
    private List<FileManageUpdateRequest> attachmentManageUpdateRequests;

    @JsonProperty("pluginConfigs")
    @ApiModelProperty(name = "pluginConfigs", value = "引擎插件列表")
    private List<PluginConfigUpdateRequest> pluginConfigUpdateRequests;
}
