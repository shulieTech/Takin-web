package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("Beanshell 前置处理器")
public class PtsApiBeanShellPreRequest implements Serializable {

    @ApiModelProperty("脚本")
    private List<String> script = new ArrayList<>();
}
