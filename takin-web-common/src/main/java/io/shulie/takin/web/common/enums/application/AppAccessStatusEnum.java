package io.shulie.takin.web.common.enums.application;

import java.util.Arrays;

import lombok.Getter;

/**
 * @author xr.l
 */

@Getter
public enum AppAccessStatusEnum {

    /**
     *
     */
    NORMAL(0,"正常"),
    UN_CONFIG(1,"待配置"),
    UN_CHECK(2,"待检测"),
    EXCEPTION(3,"异常");


    private Integer code;
    private String desc;

    AppAccessStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AppAccessStatusEnum getAppAccessStatusEnumByCode(Integer code){
        return Arrays.stream(values())
            .filter(statusEnum -> statusEnum.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }
}
