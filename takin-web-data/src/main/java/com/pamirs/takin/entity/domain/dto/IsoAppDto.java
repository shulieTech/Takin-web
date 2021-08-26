package com.pamirs.takin.entity.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IsoAppDto implements Serializable {

    //ip
    private String ip;

    //是否隔离 Y N
    private String isolationTag;

    //dubbo 注册中心
    private String dubboRegister;

    //euraka注册中心
    private String eurakaRegister;

    //rocket mq name server列表
    private List<IsoAppRocketMqDto> rockMqNameServers = new ArrayList<IsoAppRocketMqDto>();//rockMqNameServers

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIsolationTag() {
        return isolationTag;
    }

    public void setIsolationTag(String isolationTag) {
        this.isolationTag = isolationTag;
    }

    public String getDubboRegister() {
        return dubboRegister;
    }

    public void setDubboRegister(String dubboRegister) {
        this.dubboRegister = dubboRegister;
    }

    public String getEurakaRegister() {
        return eurakaRegister;
    }

    public void setEurakaRegister(String eurakaRegister) {
        this.eurakaRegister = eurakaRegister;
    }

    public List<IsoAppRocketMqDto> getRockMqNameServers() {
        return rockMqNameServers;
    }

    public void setRockMqNameServers(List<IsoAppRocketMqDto> rockMqNameServers) {
        this.rockMqNameServers = rockMqNameServers;
    }

}
