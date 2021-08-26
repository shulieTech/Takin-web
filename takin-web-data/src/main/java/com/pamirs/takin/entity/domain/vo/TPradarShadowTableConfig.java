package com.pamirs.takin.entity.domain.vo;

import java.util.List;

public class TPradarShadowTableConfig {

    /**
     * 库名
     */
    private String name;

    /**
     * 数据库 ip:port
     */
    private String url;

    /**
     * 表明列表
     */
    private List<TPradarShadowTable> tables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TPradarShadowTable> getTables() {
        return tables;
    }

    public void setTables(List<TPradarShadowTable> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "TPradarShadowTableConfig{" +
            "name='" + name + '\'' +
            ", url='" + url + '\'' +
            ", tables=" + tables +
            '}';
    }

}
