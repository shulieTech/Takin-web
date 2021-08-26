package com.pamirs.takin.entity.domain.entity.configs;

/**
 * @author fanxx
 * @date 2020/9/24 9:44 上午
 */
public class DatasourceMediator {
    private String dataSourceBusiness;
    private String dataSourcePerformanceTest;

    public String getDataSourceBusiness() {
        return dataSourceBusiness;
    }

    public void setDataSourceBusiness(String dataSourceBusiness) {
        this.dataSourceBusiness = dataSourceBusiness;
    }

    public String getDataSourcePerformanceTest() {
        return dataSourcePerformanceTest;
    }

    public void setDataSourcePerformanceTest(String dataSourcePerformanceTest) {
        this.dataSourcePerformanceTest = dataSourcePerformanceTest;
    }
}
