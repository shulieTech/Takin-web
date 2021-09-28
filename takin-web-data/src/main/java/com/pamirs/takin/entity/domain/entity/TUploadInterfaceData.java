package com.pamirs.takin.entity.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;
import org.springframework.format.annotation.DateTimeFormat;

public class TUploadInterfaceData {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_upload_interface_data.ID
     *
     * @mbggenerated
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_upload_interface_data.APP_NAME
     *
     * @mbggenerated
     */
    private String appName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_upload_interface_data.INTERFACE_VALUE
     *
     * @mbggenerated
     */
    private String interfaceValue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_upload_interface_data.INTERFACE_TYPE
     *
     * @mbggenerated
     */
    private Integer interfaceType;

    /**
     * 接口类型中文名称
     */
    private String interfaceValueName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_upload_interface_data.CREATE_TIME
     *
     * @mbggenerated
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_upload_interface_data
     *
     * @mbggenerated
     */
    public TUploadInterfaceData(Long id, String appName, String interfaceValue, Integer interfaceType,
        Date createTime) {
        this.id = id;
        this.appName = appName;
        this.interfaceValue = interfaceValue;
        this.interfaceType = interfaceType;
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_upload_interface_data
     *
     * @mbggenerated
     */
    public TUploadInterfaceData() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_upload_interface_data.ID
     *
     * @return the value of t_upload_interface_data.ID
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_upload_interface_data.ID
     *
     * @param id the value for t_upload_interface_data.ID
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_upload_interface_data.APP_NAME
     *
     * @return the value of t_upload_interface_data.APP_NAME
     * @mbggenerated
     */
    public String getAppName() {
        return appName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_upload_interface_data.APP_NAME
     *
     * @param appName the value for t_upload_interface_data.APP_NAME
     * @mbggenerated
     */
    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getInterfaceValueName() {
        return interfaceValueName;
    }

    public void setInterfaceValueName(String interfaceValueName) {
        this.interfaceValueName = interfaceValueName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_upload_interface_data.INTERFACE_VALUE
     *
     * @return the value of t_upload_interface_data.INTERFACE_VALUE
     * @mbggenerated
     */
    public String getInterfaceValue() {
        return interfaceValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_upload_interface_data.INTERFACE_VALUE
     *
     * @param interfaceValue the value for t_upload_interface_data.INTERFACE_VALUE
     * @mbggenerated
     */
    public void setInterfaceValue(String interfaceValue) {
        this.interfaceValue = interfaceValue == null ? null : interfaceValue.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_upload_interface_data.INTERFACE_TYPE
     *
     * @return the value of t_upload_interface_data.INTERFACE_TYPE
     * @mbggenerated
     */
    public Integer getInterfaceType() {
        return interfaceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_upload_interface_data.INTERFACE_TYPE
     *
     * @param interfaceType the value for t_upload_interface_data.INTERFACE_TYPE
     * @mbggenerated
     */
    public void setInterfaceType(Integer interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_upload_interface_data.CREATE_TIME
     *
     * @return the value of t_upload_interface_data.CREATE_TIME
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_upload_interface_data.CREATE_TIME
     *
     * @param createTime the value for t_upload_interface_data.CREATE_TIME
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TUploadInterfaceData{" +
            "id=" + id +
            ", appName='" + appName + '\'' +
            ", interfaceValue='" + interfaceValue + '\'' +
            ", interfaceType=" + interfaceType +
            ", createTime=" + createTime +
            '}';
    }
}
