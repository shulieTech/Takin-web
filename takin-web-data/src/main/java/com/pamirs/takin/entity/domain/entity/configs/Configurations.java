package com.pamirs.takin.entity.domain.entity.configs;

import java.util.List;

/**
 * @author fanxx
 * @date 2020/9/24 9:43 上午
 */
public class Configurations {

    private com.pamirs.takin.entity.domain.entity.configs.DatasourceMediator datasourceMediator;
    private List<DataSource> dataSources;

    public com.pamirs.takin.entity.domain.entity.configs.DatasourceMediator getDatasourceMediator() {
        return datasourceMediator;
    }

    public void setDatasourceMediator(DatasourceMediator datasourceMediator) {
        this.datasourceMediator = datasourceMediator;
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
