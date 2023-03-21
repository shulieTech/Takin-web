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

    @ApiModelProperty(value = "General")
    private GeneralResponse general = new GeneralResponse();

    @ApiModelProperty(value = "请求详情")
    private PtsRequestResponse requestData = new PtsRequestResponse();

    @ApiModelProperty(value = "响应详情")
    private PtsReturnResponse responseData = new PtsReturnResponse();
}
