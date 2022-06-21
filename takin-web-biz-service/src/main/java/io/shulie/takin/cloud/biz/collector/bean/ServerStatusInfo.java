package io.shulie.takin.cloud.biz.collector.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务器状态实体类
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-05-11 14:29
 */
@Getter
@Setter
public class ServerStatusInfo {

    private String ip;
    private float cpu;
    private long memery;
    private LoadInfo loadInfo;
    private String io;
    private List<DiskUsage> diskUsages;

    @Override
    public String toString() {
        return "ServerStatusInfo{" +
            "ip='" + ip + '\'' +
            ", cpu=" + cpu +
            ", memery=" + memery +
            ", loadInfo=" + loadInfo +
            ", io='" + io + '\'' +
            ", diskUsages=" + diskUsages +
            '}';
    }
}
