package com.pamirs.takin.entity.domain.vo;

/**
 * 说明：链路检测实体类
 *
 * @author shoaqi
 * @version V1.0
 * @date 2019年1月11日
 */
public class TLinkNodesVo {
    //@Field tLinkId : 链路id
    private String tLinkId;

    //@Field tLinkId : 链路顺序
    private Integer tLinkOrder;

    //@Field tLinkId : 链路级别
    private Integer tLinkBank;

    //@Field tLinkId : 链路名称
    private String linkName;

    public String gettLinkId() {
        return tLinkId;
    }

    public void settLinkId(String tLinkId) {
        this.tLinkId = tLinkId;
    }

    public Integer gettLinkOrder() {
        return tLinkOrder;
    }

    public void settLinkOrder(Integer tLinkOrder) {
        this.tLinkOrder = tLinkOrder;
    }

    public Integer gettLinkBank() {
        return tLinkBank;
    }

    public void settLinkBank(Integer tLinkBank) {
        this.tLinkBank = tLinkBank;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    @Override
    public String toString() {
        return "TLinkNodesVo{" +
            "tLinkId='" + tLinkId + '\'' +
            ", tLinkOrder=" + tLinkOrder +
            ", tLinkBank=" + tLinkBank +
            ", linkName='" + linkName + '\'' +
            '}';
    }

}
