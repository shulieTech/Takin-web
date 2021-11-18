package io.shulie.takin.web.biz.pojo.request.pradar;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("pradar配置修改")
public class PradarZkConfigUpdateRequest {

    @NotNull(message = "配置ID" + AppConstants.MUST_BE_NOT_NULL)
    @ApiModelProperty("配置ID")
    private Long id;

    @ApiModelProperty("值类型:[String,Int,Boolean]")
    private String value;

    @ApiModelProperty("配置类型")
    private String type;

    @ApiModelProperty("配置说明")
    private String remark;

}
