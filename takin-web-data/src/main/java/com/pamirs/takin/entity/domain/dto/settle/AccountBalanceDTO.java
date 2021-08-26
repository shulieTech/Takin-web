package com.pamirs.takin.entity.domain.dto.settle;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/9 下午5:55
 */
@Data
@ApiModel(description = "流量明细")
public class AccountBalanceDTO implements Serializable {

    private static final long serialVersionUID = -5305861767482068856L;

    @ApiModelProperty(value = "时间")
    private String gmtCreate;

    @ApiModelProperty(value = "明细")
    private String flowAmount;

    @ApiModelProperty(value = "余额")
    private String leftAmount;

    @ApiModelProperty(value = "类型")
    private String sceneCode;

    @ApiModelProperty(value = "备注")
    private String remark;
}
