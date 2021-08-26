package com.pamirs.takin.entity.domain.query;

/**
 * Created by Windows User on 2019/5/7.
 */
public class ComboxItem {

    private String code;

    private String value;

    public ComboxItem() {}

    public ComboxItem(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public ComboxItem(Integer code, String value) {
        this.code = String.valueOf(code);
        this.value = value;
    }

    public ComboxItem(Long code, String value) {
        this.code = String.valueOf(code);
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
