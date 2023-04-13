package io.shulie.takin.cloud.common.enums.pts;

public enum PtsContentTypeEnum {

    JSON("JSON", "application/json"),
    FORM("form-data", "multipart/form-data"),
    URL_ENCODE("x-www-form-urlencoded", "application/x-www-form-urlencoded");

    private String type;

    private String value;

    PtsContentTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
