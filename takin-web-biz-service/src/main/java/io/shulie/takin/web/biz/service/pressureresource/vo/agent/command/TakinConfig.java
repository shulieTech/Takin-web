package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;

/**
 * @author guann1n9
 * @date 2022/9/14 10:12 PM
 */
public class TakinConfig implements Serializable {

    /**
     * resource id 确保唯一
     */
    private String configId;


    private String configType;


    private String tenantCode;


    private String envCode;


    private String appName;


    private String agentSpecification;


    private String configParam;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigParam() {
        return configParam;
    }

    public void setConfigParam(String configParam) {
        this.configParam = configParam;
    }

    public String getAgentSpecification() {
        return agentSpecification;
    }

    public void setAgentSpecification(String agentSpecification) {
        this.agentSpecification = agentSpecification;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
