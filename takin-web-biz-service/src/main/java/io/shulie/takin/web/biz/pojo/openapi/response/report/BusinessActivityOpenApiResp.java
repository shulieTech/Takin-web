package io.shulie.takin.web.biz.pojo.openapi.response.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@ApiModel
@Data
public class BusinessActivityOpenApiResp implements Serializable {

    @ApiModelProperty(value = "活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "活动名称")
    private String businessActivityName;
}
