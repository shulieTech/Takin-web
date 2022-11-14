package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;
import java.util.List;

public class JdbcTableCompareCommand implements Serializable {

    private Integer shadowType;
    private List<String> tables;
    private DataSourceEntity bizDataSource;
    private DataSourceEntity shadowDataSource;

    public Integer getShadowType() {
        return shadowType;
    }

    public void setShadowType(Integer shadowType) {
        this.shadowType = shadowType;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public DataSourceEntity getBizDataSource() {
        return bizDataSource;
    }

    public void setBizDataSource(DataSourceEntity bizDataSource) {
        this.bizDataSource = bizDataSource;
    }

    public DataSourceEntity getShadowDataSource() {
        return shadowDataSource;
    }

    public void setShadowDataSource(DataSourceEntity shadowDataSource) {
        this.shadowDataSource = shadowDataSource;
    }
}
