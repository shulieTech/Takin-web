package com.pamirs.takin.entity.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * application relationship with Ip config
 *
 * @author shulie
 * @version v1.0
 * @2018年5月17日
 */
public class TApplicationIp implements Serializable {
    //序列号
    private static final long serialVersionUID = 6134713629206586141L;

    /**
     * id
     */
    String id;

    /**
     * applicationName
     */
    String applicationName;

    /**
     * type
     */
    String type;

    /**
     * system name
     */
    String systemName;

    /**
     * ip
     */
    String ip;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

    /**
     * 2018年5月17日
     *
     * @return the id
     * @author shulie
     * @version 1.0
     */
    public String getId() {
        return id;
    }

    /**
     * 2018年5月17日
     *
     * @param id the id to set
     * @author shulie
     * @version 1.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 2018年5月17日
     *
     * @return the applicationName
     * @author shulie
     * @version 1.0
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 2018年5月17日
     *
     * @param applicationName the applicationName to set
     * @author shulie
     * @version 1.0
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 2018年5月17日
     *
     * @return the type
     * @author shulie
     * @version 1.0
     */
    public String getType() {
        return type;
    }

    /**
     * 2018年5月17日
     *
     * @param type the type to set
     * @author shulie
     * @version 1.0
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 2018年5月17日
     *
     * @return the systemName
     * @author shulie
     * @version 1.0
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * 2018年5月17日
     *
     * @param systemName the systemName to set
     * @author shulie
     * @version 1.0
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * 2018年5月17日
     *
     * @return the ip
     * @author shulie
     * @version 1.0
     */
    public String getIp() {
        return ip;
    }

    /**
     * 2018年5月17日
     *
     * @param ip the ip to set
     * @author shulie
     * @version 1.0
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

}
