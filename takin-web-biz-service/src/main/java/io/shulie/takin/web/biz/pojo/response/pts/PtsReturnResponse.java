package io.shulie.takin.web.biz.pojo.response.pts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.web.biz.pojo.request.pts.KeyValueRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsRequestResponse
 * @Description
 * @createTime 2023年03月15日 18:23
 */
@Data
@ApiModel("调试详情-响应详情返参")
public class PtsReturnResponse implements Serializable {

    @ApiModelProperty(value = "Response Header")
    private List<KeyValueRequest> responseHeaders = new ArrayList<>();

    @ApiModelProperty(value = "Response Body结构化")
    private String responseBody;

    @ApiModelProperty(value = "Response 原文")
    private String responseOrigData;

    @ApiModelProperty(value = "Error 信息")
    private String errorMessage;
}
