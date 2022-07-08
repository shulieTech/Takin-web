package io.shulie.takin.web.biz.pojo.request.placeholdermanage;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("入参类 --> 占位符入参类")
public class PlaceholderManageRequest {

    private Long Id;

    @ApiModelProperty("占位符标识")
    private String placeholderKey;

    @ApiModelProperty("占位符替换值")
    private String placeholderValue;

    @ApiModelProperty("备注")
    private String remark;
}
