package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "PerformanceDataFileRequest")
public class PerformanceDataFileRequest implements Serializable {
    @NotNull
    @ApiModelProperty(name = "id", value = "接口压测Id")
    private Long id;

    @ApiModelProperty(name = "relatedFiles", value = "数据文件")
    @JsonProperty("relatedFiles")
    private List<FileManageUpdateRequest> fileManageUpdateRequests;

    @ApiModelProperty(name = "paramList", value = "参数信息")
    @JsonProperty("paramList")
    List<PerformanceParamRequest> paramList;
}
