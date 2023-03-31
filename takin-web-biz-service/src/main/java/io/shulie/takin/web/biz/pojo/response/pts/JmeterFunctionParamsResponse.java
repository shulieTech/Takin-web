package io.shulie.takin.web.biz.pojo.response.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("Jmeter函数参数")
public class JmeterFunctionParamsResponse implements Serializable {

    @ApiModelProperty("参数")
    private String param;
    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("是否必填：true-是 false-否")
    private Boolean required;


}
