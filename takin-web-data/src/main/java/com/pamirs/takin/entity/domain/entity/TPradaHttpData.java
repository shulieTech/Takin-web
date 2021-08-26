package com.pamirs.takin.entity.domain.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 说明: prada获取http接口表
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2019/3/4 19:59
 */
public class TPradaHttpData {

    /**
     * 表id
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private Long tphdId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 接口类型
     */
    private String interfaceType;

    /**
     * 接口类型中文名称
     */
    private String interfaceValueName;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    public String getInterfaceValueName() {
        return interfaceValueName;
    }

    public void setInterfaceValueName(String interfaceValueName) {
        this.interfaceValueName = interfaceValueName;
    }

    public Long getTphdId() {
        return tphdId;
    }

    public void setTphdId(Long tphdId) {
        this.tphdId = tphdId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TPradaHttpData{" +
            "tphdId=" + tphdId +
            ", appName='" + appName + '\'' +
            ", interfaceName='" + interfaceName + '\'' +
            ", interfaceType='" + interfaceType + '\'' +
            ", interfaceValueName='" + interfaceValueName + '\'' +
            ", createTime=" + createTime +
            '}';
    }
}
