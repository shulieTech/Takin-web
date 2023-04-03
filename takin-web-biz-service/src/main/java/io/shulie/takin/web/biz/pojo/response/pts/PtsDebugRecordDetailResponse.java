package io.shulie.takin.web.biz.pojo.response.pts;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsDebugRecordDetailResponse
 * @Description
 * @createTime 2023年03月15日 18:17
 */
@Data
@ApiModel("调试详情返参")
public class PtsDebugRecordDetailResponse implements Serializable {

    @ApiModelProperty(value = "请求时间")
    private String requestTime;

    @ApiModelProperty(value = "请求耗时，单位ms")
    private String requestCost;

    @ApiModelProperty(value = "响应状态")
    private Boolean responseStatus;

    @ApiModelProperty(value = "API名称")
    private String apiName;

    @ApiModelProperty(value = "General")
    private GeneralResponse general = new GeneralResponse();

    @ApiModelProperty(value = "请求详情")
    private PtsRequestResponse requestData = new PtsRequestResponse();

    @ApiModelProperty(value = "响应详情")
    private PtsReturnResponse responseData = new PtsReturnResponse();
}
