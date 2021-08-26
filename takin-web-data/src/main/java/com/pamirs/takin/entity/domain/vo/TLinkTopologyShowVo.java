package com.pamirs.takin.entity.domain.vo;

import java.util.List;
import java.util.Set;

/**
 * 链路拓扑图
 *
 * @author 298403
 */
public class TLinkTopologyShowVo {

    /**
     * 链路列表
     */
    private Set<TopologyLink> linkList;

    /**
     * 链路节点方向
     */
    private List<TopologyNode> nodeList;

    public Set<TopologyLink> getLinkList() {
        return linkList;
    }

    public void setLinkList(Set<TopologyLink> linkList) {
        this.linkList = linkList;
    }

    public List<TopologyNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<TopologyNode> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public String toString() {
        return "TLinkTopologyShowVo{" +
            "linkList=" + linkList +
            ", nodeList=" + nodeList +
            '}';
    }

}
