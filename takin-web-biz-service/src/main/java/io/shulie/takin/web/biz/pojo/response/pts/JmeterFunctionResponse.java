package io.shulie.takin.web.biz.pojo.response.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("Jmeter函数")
public class JmeterFunctionResponse implements Serializable {

    @ApiModelProperty("函数名称")
    private String functionName;

    @ApiModelProperty("函数描述")
    private String functionDesc;

    @ApiModelProperty("函数示例")
    private String functionExample;

    @ApiModelProperty("参数列表")
    private List<JmeterFunctionParamsResponse> functionParams;
}
