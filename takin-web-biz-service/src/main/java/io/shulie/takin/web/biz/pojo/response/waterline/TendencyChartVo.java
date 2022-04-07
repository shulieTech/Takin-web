package io.shulie.takin.web.biz.pojo.response.waterline;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TendencyChartVo {
    private List<Double> totalCount = new ArrayList<>();
    private List<String> disk = new ArrayList<>();
    private List<String> net = new ArrayList<>();
    private List<Double> totalTps = new ArrayList<>();
    private String agentId;
    private List<String> cpuLoad = new ArrayList<>();
    private String applicationName;
    private List<String> cpuRate = new ArrayList<>();
    private List<String> memory = new ArrayList<>();
    private int nodesNumber;
    private List<String> tags;

    public void setTotalCount(Double totalCount) {
        this.totalCount.add(totalCount);
    }

    public void setDisk(String disk) {
        this.disk.add(disk);
    }

    public void setNet(String net) {
        this.net.add(net);
    }

    public void setTotalTps(Double totalTps) {
        this.totalTps.add(totalTps);
    }

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad.add(cpuLoad);
    }

    public void setCpuRate(String cpuRate) {
        this.cpuRate.add(cpuRate);
    }

    public void setMemory(String memory) {
        this.memory.add(memory);
    }
}
