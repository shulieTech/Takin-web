package io.shulie.takin.web.biz.pojo.response.waterline;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Metrics {
    private String applicationName;
    private String cpuRate;
    private String memory;
    private int nodesNumber;
    private List<String> tags;

    public Metrics(String applicationName, String cpuRate, String memory) {
        this.applicationName = applicationName;
        this.cpuRate = cpuRate;
        this.memory = memory;
    }
}
