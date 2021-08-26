package com.pamirs.takin.entity.domain.vo.settle;

import java.io.Serializable;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/5/9 下午5:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountBalanceQueryVO extends UserCommonExt implements Serializable {
    private Long bookId;
}
