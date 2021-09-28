package com.pamirs.takin.entity.domain.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 说明：告警实体类
 *
 * @author shulie
 * @version v1.0
 * @2018年5月17日
 */
public class TAlarm implements Serializable {
    //序列号
    private static final long serialVersionUID = -7770795747678986750L;
    //主键id
    private Long id;

    // WAR包名
    private String warPackages;

    // IP
    private String ip;

    // 告警汇总
    private String alarmCollects;

    // 告警时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date alarmDate;

    // 创建时间  默认：CURRENT_TIMESTAMP
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // 更新时间  默认：CURRENT_TIMESTAMP
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

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
    public Long getId() {
        return id;
    }

    /**
     * 2018年5月17日
     *
     * @param id the id to set
     * @author shulie
     * @version 1.0
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 2018年5月17日
     *
     * @return the warPackages
     * @author shulie
     * @version 1.0
     */
    public String getWarPackages() {
        return warPackages;
    }

    /**
     * 2018年5月17日
     *
     * @param warPackages the warPackages to set
     * @author shulie
     * @version 1.0
     */
    public void setWarPackages(String warPackages) {
        this.warPackages = warPackages;
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

    /**
     * 2018年5月17日
     *
     * @return the alarmCollects
     * @author shulie
     * @version 1.0
     */
    public String getAlarmCollects() {
        return alarmCollects;
    }

    /**
     * 2018年5月17日
     *
     * @param alarmCollects the alarmCollects to set
     * @author shulie
     * @version 1.0
     */
    public void setAlarmCollects(String alarmCollects) {
        this.alarmCollects = alarmCollects;
    }

    /**
     * 2018年5月17日
     *
     * @return the alarmDate
     * @author shulie
     * @version 1.0
     */
    public Date getAlarmDate() {
        return alarmDate;
    }

    /**
     * 2018年5月17日
     *
     * @param alarmDate the alarmDate to set
     * @author shulie
     * @version 1.0
     */
    public void setAlarmDate(Date alarmDate) {
        this.alarmDate = alarmDate;
    }

    /**
     * 2018年5月17日
     *
     * @return the createTime
     * @author shulie
     * @version 1.0
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 2018年5月17日
     *
     * @param createTime the createTime to set
     * @author shulie
     * @version 1.0
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 2018年5月17日
     *
     * @return the modifyTime
     * @author shulie
     * @version 1.0
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 2018年5月17日
     *
     * @param modifyTime the modifyTime to set
     * @author shulie
     * @version 1.0
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

}
