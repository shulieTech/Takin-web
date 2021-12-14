package io.shulie.takin.web.common.enums.application;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xr.l
 */

@Getter
@AllArgsConstructor
public enum AppAccessStatusEnum {

    /**
     * 正常
     */
    NORMAL(0,"正常"),
    UN_CONFIG(1,"待配置"),
    UN_CHECK(2,"待检测"),
    EXCEPTION(3,"异常");


    private final Integer code;
    private final String desc;

    public static AppAccessStatusEnum getAppAccessStatusEnumByCode(Integer code){
        return Arrays.stream(values()).filter(statusEnum -> statusEnum.getCode().equals(code))
            .findFirst().orElse(null);
    }

}
