package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 命令模板数据传输
 * @author 710524
 * @ClassName: ChaosCommandTemplateDTO
 * @package: com.pamirs.takin.entity.domain.query
 * @date 2019/5/8 0008 15:39
 */
public class ChaosCommandTemplateDTO implements Serializable {

    //主键
    private Long id;

    //命令模板名称
    private String name;
    //关键字
    private String keyword;
    //关键字
    private String commandTemplate;
    //状态(Y=启用,N=禁用)
    private String status;

    private Byte isSystem;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    private String paramDesc;

    private Integer pluginCount;
    private String faultType;
    private String faultTypeDesc;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Byte getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Byte isSystem) {
        this.isSystem = isSystem;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getFaultType() {
        return faultType;
    }

    public ChaosCommandTemplateDTO setFaultType(String faultType) {
        this.faultType = faultType;
        return this;
    }

    public String getFaultTypeDesc() {
        return faultTypeDesc;
    }

    public ChaosCommandTemplateDTO setFaultTypeDesc(String faultTypeDesc) {
        this.faultTypeDesc = faultTypeDesc;
        return this;
    }
}
