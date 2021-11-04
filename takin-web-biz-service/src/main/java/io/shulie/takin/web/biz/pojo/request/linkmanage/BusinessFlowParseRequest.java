package io.shulie.takin.web.biz.pojo.request.linkmanage;

import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "BusinessFlowParseRequest", description = "业务流程解析脚本文件入参")
public class BusinessFlowParseRequest {

    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @NotNull
    @ApiModelProperty(name = "scriptFile", value = "脚本文件")
    private FileManageUpdateRequest scriptFile;


}
