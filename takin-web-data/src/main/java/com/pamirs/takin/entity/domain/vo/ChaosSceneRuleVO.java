package com.pamirs.takin.entity.domain.vo;

import java.util.Map;

import com.pamirs.takin.entity.domain.entity.BaseEntity;

public class ChaosSceneRuleVO extends BaseEntity {
    private Long id;

    private Long sceneId;

    private String sceneName;

    private Long commandId;

    private String command;

    private String commandParam;

    private Long pluginId;

    private String pluginName;

    private Integer continuousTime;

    private Long appServiceId;

    private String appServcieName;

    private String aswNo;

    private Integer pressureCount;

    private Integer allowErrorCount;

    private Integer isDeleted;

    private Map<String, Object> paramsMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName == null ? null : sceneName.trim();
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command == null ? null : command.trim();
    }

    public String getCommandParam() {
        return commandParam;
    }

    public void setCommandParam(String commandParam) {
        this.commandParam = commandParam;
    }

    public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName == null ? null : pluginName.trim();
    }

    public Integer getContinuousTime() {
        return continuousTime;
    }

    public void setContinuousTime(Integer continuousTime) {
        this.continuousTime = continuousTime;
    }

    public Long getAppServiceId() {
        return appServiceId;
    }

    public void setAppServiceId(Long appServiceId) {
        this.appServiceId = appServiceId;
    }

    public String getAppServcieName() {
        return appServcieName;
    }

    public void setAppServcieName(String appServcieName) {
        this.appServcieName = appServcieName;
    }

    public String getAswNo() {
        return aswNo;
    }

    public void setAswNo(String aswNo) {
        this.aswNo = aswNo;
    }

    public Integer getPressureCount() {
        return pressureCount;
    }

    public void setPressureCount(Integer pressureCount) {
        this.pressureCount = pressureCount;
    }

    public Integer getAllowErrorCount() {
        return allowErrorCount;
    }

    public void setAllowErrorCount(Integer allowErrorCount) {
        this.allowErrorCount = allowErrorCount;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }
}
