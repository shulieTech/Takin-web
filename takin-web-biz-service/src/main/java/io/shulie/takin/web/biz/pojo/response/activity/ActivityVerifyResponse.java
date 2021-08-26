package io.shulie.takin.web.biz.pojo.response.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xr.l
 */
@Data
@ApiModel
public class ActivityVerifyResponse {

    @ApiModelProperty("业务活动ID")
    private Long activityId;

    @ApiModelProperty("脚本ID")
    private Long scriptId;

    @ApiModelProperty("发起流量验证结果：false:失败；true:成功")
    private Boolean taskStatus;

    @ApiModelProperty(value = "验证状态：0/未验证，1/验证中, 2/验证完成")
    private Integer verifyStatus;

    @ApiModelProperty("流量验证状态：false:未验证；true:已验证")
    private Boolean verifiedFlag;
}
