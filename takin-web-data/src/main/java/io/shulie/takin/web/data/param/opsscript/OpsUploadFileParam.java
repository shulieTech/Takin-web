package io.shulie.takin.web.data.param.opsscript;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class OpsUploadFileParam implements Serializable {
    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "内容")
    private String content;
}
