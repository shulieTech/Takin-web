package com.pamirs.takin.entity.domain.query;

/**
 * 说明: 业务链路封装类
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2019/1/2 15:20
 */
public class TechLink {

    /**
     * 技术链路id
     */
    private String tLinkId;

    /**
     * 技术链路名称
     */
    private String tLinkName;

    /**
     * 技术链路横向编号
     */
    private String tLinkOrder;

    /**
     * 技术链路纵向编号
     */
    private String tLinkBank;

    public String gettLinkId() {
        return tLinkId;
    }

    public void settLinkId(String tLinkId) {
        this.tLinkId = tLinkId;
    }

    public String gettLinkName() {
        return tLinkName;
    }

    public void settLinkName(String tLinkName) {
        this.tLinkName = tLinkName;
    }

    public String gettLinkOrder() {
        return tLinkOrder;
    }

    public void settLinkOrder(String tLinkOrder) {
        this.tLinkOrder = tLinkOrder;
    }

    public String gettLinkBank() {
        return tLinkBank;
    }

    public void settLinkBank(String tLinkBank) {
        this.tLinkBank = tLinkBank;
    }

    @Override
    public String toString() {
        return "TechLink{" +
            "tLinkId='" + tLinkId + '\'' +
            ", tLinkName='" + tLinkName + '\'' +
            ", tLinkOrder='" + tLinkOrder + '\'' +
            ", tLinkBank='" + tLinkBank + '\'' +
            '}';
    }
}
