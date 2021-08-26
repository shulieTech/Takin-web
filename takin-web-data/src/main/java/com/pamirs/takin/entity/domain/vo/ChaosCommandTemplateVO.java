package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;

/**
 * 混沌工程命令模板VO
 *
 * @author 710524
 * @date 2019/5/8 0008 14:41
 */
public class ChaosCommandTemplateVO implements Serializable {

    private Long id;

    private String name;

    private String keyword;

    private String commandTemplate;

    private Byte isSystem;

    private String paramDesc;

    private Integer pluginCount;

    private Integer faultType;

    private Long[] pluginKeys;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCommandTemplate() {
        return commandTemplate;
    }

    public void setCommandTemplate(String commandTemplate) {
        this.commandTemplate = commandTemplate;
    }

    public Byte getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Byte isSystem) {
        this.isSystem = isSystem;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public Integer getPluginCount() {
        return pluginCount;
    }

    public void setPluginCount(Integer pluginCount) {
        this.pluginCount = pluginCount;
    }

    public Long[] getPluginKeys() {
        return pluginKeys;
    }

    public void setPluginKeys(Long[] pluginKeys) {
        this.pluginKeys = pluginKeys;
    }

    public Integer getFaultType() {
        return faultType;
    }

    public ChaosCommandTemplateVO setFaultType(Integer faultType) {
        this.faultType = faultType;
        return this;
    }
}
