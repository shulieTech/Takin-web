package com.pamirs.takin.entity.domain.vo;

import java.util.List;

/**
 * app isolation vo
 */
public class AppIsolationVo {

    /**
     * appName
     */
    private String applicationName;

    /**
     * ip list from php interface (ip can be isolated)
     */
    private List<String> appNodes;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public List<String> getAppNodes() {
        return appNodes;
    }

    public void setAppNodes(List<String> appNodes) {
        this.appNodes = appNodes;
    }
}
