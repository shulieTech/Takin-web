package io.shulie.takin.web.biz.pojo.response.waterline;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TendencyChart extends Metrics {
    private double totalCount;
    private String disk;
    private String net;
    private double totalTps;
    private String hostIp;
    private String time;
    private String cpuLoad;
}
