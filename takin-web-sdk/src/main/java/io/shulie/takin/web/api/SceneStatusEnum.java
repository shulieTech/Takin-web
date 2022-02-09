package io.shulie.takin.web.api;

/**
 * @author caijianying
 */

public enum SceneStatusEnum {
    PEDDING(0, "待启动"),
    PRESSURE_TESTING(1, "压测中"),
    FAIL(2, "启动压测失败");

    private int status;
    private String value;

    SceneStatusEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
