package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;

public class IsoAppRocketMqDto implements Serializable {
    //域 doman
    private String clusterName;

    //name server
    private String nameServer;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }
}
