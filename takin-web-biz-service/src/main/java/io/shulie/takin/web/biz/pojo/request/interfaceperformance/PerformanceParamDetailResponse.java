package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 2:32 下午
 */
@Data
public class PerformanceParamDetailResponse {
    @NotNull
    @ApiModelProperty(name = "id", value = "接口压测Id")
    private Long configId;

    /**
     * 文件列表
     */
    @ApiModelProperty(value = "文件列表")
    @JsonProperty("relatedFiles")
    private List<FileManageResponse> fileManageResponseList;

    @ApiModelProperty(name = "paramList", value = "参数信息")
    @JsonProperty("paramList")
    List<PerformanceParamRequest> paramList;
}
