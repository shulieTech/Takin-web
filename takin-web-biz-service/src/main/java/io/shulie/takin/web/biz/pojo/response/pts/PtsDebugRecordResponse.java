package io.shulie.takin.web.biz.pojo.response.pts;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junshi
 * @ClassName PtsDebugRecordResponse
 * @Description
 * @createTime 2023年03月15日 18:17
 */
@Data
@ApiModel("调试记录返参")
public class PtsDebugRecordResponse implements Serializable {

    @ApiModelProperty(value = "API名称")
    private String apiName;

    @ApiModelProperty(value = "请求时间")
    private String requestTime;

    @ApiModelProperty(value = "响应状态")
    private String responseStatus;
}
