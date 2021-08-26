package com.pamirs.takin.entity.domain.vo;

import java.util.List;

public class TPradarShadowTable {

    /**
     * 操作类型
     */
    private List<String> opType;

    /**
     * 表明
     */
    private String tableName;

    public List<String> getOpType() {
        return opType;
    }

    public void setOpType(List<String> opType) {
        this.opType = opType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "TPradarShadowTable{" +
            "opType='" + opType + '\'' +
            ", tableName='" + tableName + '\'' +
            '}';
    }

}
