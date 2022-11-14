package io.shulie.takin.web.biz.service.pressureresource.common;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/6 9:48 AM
 */
public enum ModuleEnum {
    ALL("ALL"),
    APP("APP"),
    DS("DS"),
    REMOTECALL("REMOTECALL"),
    MQ("MQ");

    String code;

    ModuleEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
