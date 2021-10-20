package io.shulie.takin.web.data.result.baseserver;


import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Data
@Measurement(name = "trace_metrics")
public class TraceMetricsResult {
    @Column(name = "time")
    private Instant time;

    @Column(name = "errorCount")
    private Double errorCount;

    @Column(name = "hitCount")
    private Double hitCount;

    @Column(name = "log_time")
    private String logTime;

    @Column(name = "successCount")
    private Double successCount;
    /** 别名 */
    @Column(name = "allSuccessCount")
    private Double allSuccessCount;

    @Column(name = "total")
    private Double total;

    @Column(name = "totalCount")
    private Double totalCount;
    /** 别名 */
    @Column(name = "allTotalCount")
    private Double allTotalCount;

    @Column(name = "totalQps")
    private Double totalQps;

    @Column(name = "totalRt")
    private Double totalRt;

    /** 别名 */
    @Column(name = "allTotalRt")
    private Double allTotalRt;
    /** 别名 */
    @Column(name = "allMaxRt")
    private Double allMaxRt;

    @Column(name = "totalTps")
    private Double totalTps;
    /** 别名 */
    @Column(name = "allTotalTps")
    private Double allTotalTps;

    @Column(name = "traceId")
    private String traceId;

    @Column(name = "appName", tag = true)
    private String appName;

    @Column(name = "clusterTest", tag = true)
    private String clusterTest;

    @Column(name = "edgeId", tag = true)
    private String edgeId;

    @Column(name = "logType", tag = true)
    private String logType;

    @Column(name = "method", tag = true)
    private String method;

    @Column(name = "middlewareName", tag = true)
    private String middlewareName;

    @Column(name = "rpcType", tag = true)
    private String rpcType;

    @Column(name = "serverAppName", tag = true)
    private String serverAppName;

    @Column(name = "service", tag = true)
    private String service;
}
