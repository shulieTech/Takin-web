package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;

/**
 * 插件查询
 *
 * @author 710524
 */
public class ChaosPluginVO implements Serializable {

    private Long id;

    private Long appId;

    private Integer pluginType;

    private String pluginName;

    private String pluginPackageName;

    private String pluginPackageVersion;

    private String pluginFile;

    private String note;

    public Integer getPluginType() {
        return pluginType;
    }

    public void setPluginType(Integer pluginType) {
        this.pluginType = pluginType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginPackageName() {
        return pluginPackageName;
    }

    public void setPluginPackageName(String pluginPackageName) {
        this.pluginPackageName = pluginPackageName;
    }

    public String getPluginPackageVersion() {
        return pluginPackageVersion;
    }

    public void setPluginPackageVersion(String pluginPackageVersion) {
        this.pluginPackageVersion = pluginPackageVersion;
    }

    public String getPluginFile() {
        return pluginFile;
    }

    public void setPluginFile(String pluginFile) {
        this.pluginFile = pluginFile;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
