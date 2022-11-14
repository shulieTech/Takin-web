package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;

/**
 * @author guann1n9
 * @date 2022/9/14 10:16 AM
 */
public class TakinCommand implements Serializable {

    public static final String SIMULATOR_AGENT = "simulator-agent";

    /**
     * resource id 确保唯一
     */
    private String commandId;


    private String commandType;


    private String tenantCode;


    private String agentSpecification;


    private String envCode;


    private String appName;

    private String nacosServerAddr;

    private String commandParam;


    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getCommandType() {
        return commandType;
    }

    public String getAgentSpecification() {
        return agentSpecification;
    }

    public void setAgentSpecification(String agentSpecification) {
        this.agentSpecification = agentSpecification;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
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

    public String getCommandParam() {
        return commandParam;
    }

    public void setCommandParam(String commandParam) {
        this.commandParam = commandParam;
    }

    public String getNacosServerAddr() {
        return nacosServerAddr;
    }

    public void setNacosServerAddr(String nacosServerAddr) {
        this.nacosServerAddr = nacosServerAddr;
    }
}
