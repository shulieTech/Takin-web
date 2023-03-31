package io.shulie.takin.web.biz.pojo.response.pts;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName GeneralResponse
 * @Description
 * @createTime 2023年03月15日 18:23
 */
@Data
@ApiModel("调试详情-General返参")
public class GeneralResponse implements Serializable {

    @ApiModelProperty(value = "Request URL")
    private String requestUrl;

    @ApiModelProperty(value = "Request Method")
    private String requestMethod;

    @ApiModelProperty(value = "Response Code")
    private String responseCode;

    @ApiModelProperty(value = "Export Content")
    private String exportContent;

    @ApiModelProperty(value = "Check Result")
    private String checkResult;
}
