package com.pamirs.takin.entity.domain.vo.account;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.pamirs.takin.entity.domain.PagingDevice;

/**
 * @author qianshui
 * @date 2020/5/12 下午8:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountQueryVO extends PagingDevice implements Serializable {

    private static final long serialVersionUID = 70999071704068714L;

}
