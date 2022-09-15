package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;
import java.util.List;

/**
 * @author guann1n9
 * @date 2022/9/14 8:13 PM
 */
public class JdbcTableConfig implements Serializable {

    private List<DataSourceConfig> data;


    public List<DataSourceConfig> getData() {
        return data;
    }

    public void setData(List<DataSourceConfig> data) {
        this.data = data;
    }
}
