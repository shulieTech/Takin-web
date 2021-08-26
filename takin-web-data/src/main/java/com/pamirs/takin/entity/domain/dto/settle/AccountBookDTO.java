package com.pamirs.takin.entity.domain.dto.settle;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/12 下午7:56
 */
@Data
@ApiModel("流量账本")
public class AccountBookDTO implements Serializable {

    private static final long serialVersionUID = -1791348292712405730L;

    private BigDecimal balance;

    private BigDecimal lockBalance;

    private BigDecimal totalBalance;
}
