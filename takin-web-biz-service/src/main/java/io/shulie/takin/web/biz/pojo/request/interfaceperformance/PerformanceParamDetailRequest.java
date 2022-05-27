package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 2:32 下午
 */
@Data
public class PerformanceParamDetailRequest {
    private Long configId;

    @ApiModelProperty(name = "uploadFiles", value = "数据文件")
    @JsonProperty("uploadFiles")
    private List<FileManageUpdateRequest> fileManageUpdateRequests;
}
