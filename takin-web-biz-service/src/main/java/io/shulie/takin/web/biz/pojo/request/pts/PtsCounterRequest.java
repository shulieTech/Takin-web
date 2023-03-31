package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("计数器入参")
public class PtsCounterRequest implements Serializable {

    @ApiModelProperty("起始值")
    private String start;

    @ApiModelProperty("结束值")
    private String end;

    @ApiModelProperty("递增值")
    private String incr;

    @ApiModelProperty("变量名称")
    private String name;

    @ApiModelProperty("格式")
    private String format;
}
