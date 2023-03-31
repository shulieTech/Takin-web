package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "HTTP请求默认值")
public class PtsGlobalHttpRequest implements Serializable {

    @ApiModelProperty(value = "服务器名称或IP")
    private String domain;

    @ApiModelProperty(value = "端口号")
    private String port;

    @ApiModelProperty(value = "协议HTTP|HTTPS")
    private String protocol;

    @ApiModelProperty(value = "请求path")
    private String path;

    @ApiModelProperty(value = "内容编码")
    private String contentEncoding;
}
