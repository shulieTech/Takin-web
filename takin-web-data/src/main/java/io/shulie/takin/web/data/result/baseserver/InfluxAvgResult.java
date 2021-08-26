package io.shulie.takin.web.data.result.baseserver;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Data
@Measurement(name = "takin_pradar")
public class InfluxAvgResult {
    @Column(name = "rt")
    private Double rt;
    @Column(name = "tps")
    private Double tps;
    @Column(name = "count")
    private Double count;
    @Column(name = "errorCount")
    private Double errorCount;
    @Column(name = "maxRt")
    private Double maxRt;
    @Column(name = "minRt")
    private Double minRt;
    @Column(name = "traceId")
    private String traceId;
}
