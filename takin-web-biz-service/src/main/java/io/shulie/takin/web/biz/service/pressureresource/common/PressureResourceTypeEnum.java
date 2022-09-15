package io.shulie.takin.web.biz.service.pressureresource.common;

import org.springframework.util.StringUtils;

/**
 * @author guann1n9
 * @date 2022/9/14 6:52 PM
 */
public enum PressureResourceTypeEnum {

    /**
     * 数据库
     */
    DATABASE("pressure_database"),

    /**
     * 白名单
     */
    WHITELIST("pressure_whitelist");


    private String code;


    PressureResourceTypeEnum(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }


    public static PressureResourceTypeEnum getByCode(String code){
        if(!StringUtils.hasText(code)){
            return null;
        }
        for (PressureResourceTypeEnum value : PressureResourceTypeEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
}
