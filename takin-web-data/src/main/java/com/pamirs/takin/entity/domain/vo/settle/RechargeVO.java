package com.pamirs.takin.entity.domain.vo.settle;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/5/9 下午4:47
 */
@Data
@ApiModel(description = "充值流量")
public class RechargeVO implements Serializable {

    private static final long serialVersionUID = -2495969329977126310L;

    @NotNull
    @ApiModelProperty(value = "客户ID")
    private Long customerId;

    @NotNull
    @ApiModelProperty(value = "充值现金")
    private BigDecimal cashAmount;

    @NotNull
    @ApiModelProperty(value = "客户流量")
    private BigDecimal flowAmount;
}
