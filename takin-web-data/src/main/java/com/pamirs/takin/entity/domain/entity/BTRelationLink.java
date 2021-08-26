package com.pamirs.takin.entity.domain.entity;

/**
 * 说明: 业务链路和技术链路关联关系实体
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/12/26 17:01
 */
public class BTRelationLink extends AbstractRelationLinkModel {

    /**
     * 目标表
     */
    private String objTable;

    public String getObjTable() {
        return objTable;
    }

    public void setObjTable(String objTable) {
        this.objTable = objTable;
    }

    @Override
    public String toString() {
        return "BTRelationLink{" +
            "objTable='" + objTable + '\'' +
            ", parentLinkId=" + parentLinkId +
            ", childLinkId=" + childLinkId +
            ", linkOrder=" + linkOrder +
            ", linkBank=" + linkBank +
            '}';
    }
}
