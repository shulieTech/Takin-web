package io.shulie.takin.web.common.common;

import java.util.Arrays;

import io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author caijianying
 */

public enum Separator {
    Separator1(1,"/"),
    Separator2(2,":"),
    Separator3(3,"_"),
    Separator4(4,"-");

    private int code;
    private String value;

    Separator(int code, String value) {
        this.code=code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 默认使用冒号 :
     * @return
     */
    public static Separator defautSeparator(){
        return Separator2;
    }
}
