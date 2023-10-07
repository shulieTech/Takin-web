package io.shulie.takin.web.biz.pojo.response.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("调试结果返回")
public class PtsDebugResponse implements Serializable {

    @ApiModelProperty("结果是否已返回：true-是 false-否，继续查询")
    private Boolean hasResult = false;

    @ApiModelProperty("是否有异常：true-是 false-否，异常弹框，显示异常信息")
    private Boolean hasException = false;

    @ApiModelProperty("异常信息")
    private String exceptionMessage;

    @ApiModelProperty("调试结果列表")
    private List<PtsDebugRecordResponse> records = new ArrayList<>();
}