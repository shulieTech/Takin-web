package io.shulie.takin.web.biz.pojo.request.pts;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsApiBaseRequest
 * @Description
 * @createTime 2023年03月15日 15:34
 */
@Data
@ApiModel("新增业务流程-串联链路-API-基本请求信息入参")
public class PtsApiBaseRequest implements Serializable {

    @ApiModelProperty(value = "请求URL", required = true)
    @NotBlank(message = "请求URL不能为空")
    private String requestUrl;

    @ApiModelProperty(value = "请求方法,GET POST", required = true)
    @NotBlank(message = "请求方法不能为空")
    private String requestMethod;

    @ApiModelProperty(value = "超时时间，单位ms")
    private Integer requestTimeout;

    @ApiModelProperty(value = "允许302跳转，false-否 true-是")
    private Boolean allowForward = true;

    @ApiModelProperty(value = "是否使用KeepAlive，false-否 true-是")
    private Boolean keepAlive = false;
}
