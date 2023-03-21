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
@ApiModel("调试详情-请求详情返参")
public class PtsRequestResponse implements Serializable {

    @ApiModelProperty(value = "Request Header")
    private List<KeyValueRequest> requestHeaders = new ArrayList<>();

    @ApiModelProperty(value = "Request Body结构化")
    private String requestBody;

    @ApiModelProperty(value = "Request 原始报文")
    private String requestOrigData;
}
