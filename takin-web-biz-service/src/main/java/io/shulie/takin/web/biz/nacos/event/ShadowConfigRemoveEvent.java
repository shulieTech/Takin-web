package io.shulie.takin.web.biz.nacos.event;

public class ShadowConfigRemoveEvent {
    private String appName;
    private String clusterName;

    public ShadowConfigRemoveEvent(String appName, String clusterName) {
        this.appName = appName;
        this.clusterName = clusterName;
    }


    public String getClusterName() {
        return clusterName;
    }

    public String getAppName() {
        return appName;
    }

}
