package com.pamirs.takin.entity.domain.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import org.springframework.format.annotation.DateTimeFormat;

public class TShadowTableConfigVo implements Serializable {

    private static final long serialVersionUID = -7033824380871303262L;

    /**
     * 影子表主键id
     */
    private String id;
    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 数据库 ip 端口port
     */
    private String databaseIpPort;

    /**
     * 数据库表明
     */
    private String databaseName;

    /**
     * 影子库表名
     */
    private String shadowTableName;

    /**
     * 使用状态
     */
    private Integer enableStatus;

    /**
     * 影子表数据源id
     */
    private String shadowDatasourceId;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    private Integer useShadowTable;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDatabaseIpPort() {
        return databaseIpPort;
    }

    public void setDatabaseIpPort(String databaseIpPort) {
        this.databaseIpPort = databaseIpPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getShadowTableName() {
        return shadowTableName;
    }

    public void setShadowTableName(String shadowTableName) {
        this.shadowTableName = shadowTableName;
    }

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShadowDatasourceId() {
        return shadowDatasourceId;
    }

    public void setShadowDatasourceId(String shadowDatasourceId) {
        this.shadowDatasourceId = shadowDatasourceId;
    }

    public Integer getUseShadowTable() {
        return useShadowTable;
    }

    public void setUseShadowTable(Integer useShadowTable) {
        this.useShadowTable = useShadowTable;
    }

    @Override
    public String toString() {
        return "TShadowTableConfigVo{" +
            "id='" + id + '\'' +
            ", applicationName='" + applicationName + '\'' +
            ", applicationId='" + applicationId + '\'' +
            ", databaseIpPort='" + databaseIpPort + '\'' +
            ", databaseName='" + databaseName + '\'' +
            ", shadowTableName='" + shadowTableName + '\'' +
            ", enableStatus=" + enableStatus +
            ", shadowDatasourceId='" + shadowDatasourceId + '\'' +
            ", createTime=" + createTime +
            ", useShadowTable=" + useShadowTable +
            '}';
    }

}
