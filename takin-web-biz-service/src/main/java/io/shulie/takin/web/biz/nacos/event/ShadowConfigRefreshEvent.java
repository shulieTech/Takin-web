package io.shulie.takin.web.biz.nacos.event;

public class ShadowConfigRefreshEvent {
    private String appName;

    public ShadowConfigRefreshEvent(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }
}
