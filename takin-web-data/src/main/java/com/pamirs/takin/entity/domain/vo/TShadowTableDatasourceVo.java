package com.pamirs.takin.entity.domain.vo;

/**
 * @author 298403
 * 影子库数据源
 */
public class TShadowTableDatasourceVo {

    /**
     * 影子数据源id
     */
    private String shadowDatasourceId;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 数据库ip端口
     */
    private String databaseIpport;

    /**
     * 数据库库名
     */
    private String databaseName;

    /**
     * 是否使用影子表 1 使用 0不使用
     */
    private Integer useShadowTable;

    public String getShadowDatasourceId() {
        return shadowDatasourceId;
    }

    public void setShadowDatasourceId(String shadowDatasourceId) {
        this.shadowDatasourceId = shadowDatasourceId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDatabaseIpport() {
        return databaseIpport;
    }

    public void setDatabaseIpport(String databaseIpport) {
        this.databaseIpport = databaseIpport;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Integer getUseShadowTable() {
        return useShadowTable;
    }

    public void setUseShadowTable(Integer useShadowTable) {
        this.useShadowTable = useShadowTable;
    }

    @Override
    public String toString() {
        return "TShadowTableDatasourceVo{" +
            "shadowDatasourceId='" + shadowDatasourceId + '\'' +
            ", applicationId='" + applicationId + '\'' +
            ", databaseIpport='" + databaseIpport + '\'' +
            ", databaseName='" + databaseName + '\'' +
            ", useShadowTable=" + useShadowTable +
            '}';
    }

}
