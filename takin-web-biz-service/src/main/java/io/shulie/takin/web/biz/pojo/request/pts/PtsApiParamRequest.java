package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PtsApiParamRequest implements Serializable {

    @ApiModelProperty(value = "参数定义")
    private List<JavaParamRequest> params = new ArrayList<>();
}
