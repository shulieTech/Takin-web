package io.shulie.takin.web.biz.pojo.response.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("JavaRequest参数")
public class JmeterJavaRequestResponse implements Serializable {

    @ApiModelProperty("java类型: IB2")
    private String javaType;

    @ApiModelProperty("类名")
    private String className;

    @ApiModelProperty("入参列表")
    private List<JmeterJavaRequestParamsResponse> params = new ArrayList<>();
}
