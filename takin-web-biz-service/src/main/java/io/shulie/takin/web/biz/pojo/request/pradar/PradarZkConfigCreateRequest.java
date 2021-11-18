package io.shulie.takin.web.biz.pojo.request.pradar;

import javax.validation.constraints.NotBlank;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("pradar配置新增")
public class PradarZkConfigCreateRequest implements AppConstants {

    @NotBlank(message = "配置路径" + MUST_BE_NOT_NULL)
    @ApiModelProperty("配置路径")
    private String zkPath;

    @NotBlank(message = "类型" + MUST_BE_NOT_NULL)
    @ApiModelProperty("值类型:[String,Int,Boolean]")
    private String type;

    @NotBlank(message = "配置值" + MUST_BE_NOT_NULL)
    @ApiModelProperty("配置值")
    private String value;

    @ApiModelProperty("配置说明")
    private String remark;

}
