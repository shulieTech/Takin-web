package io.shulie.takin.web.common.enums.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/5/28 12:08 下午
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {
    NORMAL_BUSINESS(0,"正常业务活动"),
    VIRTUAL_BUSINESS(1,"虚拟业务活动");
    private Integer type;
    private String desc;

}
