package io.shulie.takin.web.biz.service.pressureresource.common;

import org.springframework.util.StringUtils;

/**
 * @author guann1n9
 * @date 2022/10/17 7:15 PM
 */
public enum MqTypeEnum {

    /**
     *
     */
    SF_KAKFA("KAFKA-其他");

    private String code;

    MqTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public static MqTypeEnum getByCode(String code){
        if(!StringUtils.hasText(code)){
            return null;
        }
        for (MqTypeEnum value : MqTypeEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

}
