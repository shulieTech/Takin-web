package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("函数调试")
public class FunctionDebugRequest implements Serializable {

    @ApiModelProperty(value = "调试函数", required = true)
    @NotNull(message = "函数不能为空")
    private String funcStr;
}
