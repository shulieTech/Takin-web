package io.shulie.takin.web.biz.pojo.response.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("JavaRequestParams参数")
public class JmeterJavaRequestParamsResponse implements Serializable {

    @ApiModelProperty("请求中文描述")
    private String paramCNDesc;

    @ApiModelProperty("请求参数")
    private String paramName;

    @ApiModelProperty("请求值")
    private String paramValue;

    @ApiModelProperty("参数类型：text-文本 file-文件")
    private String paramType;

    @ApiModelProperty("是否必填： true-是,请求值填写到input框 false-否,请求值Tips")
    private Boolean require = false;

    private Integer sortNum;

}
