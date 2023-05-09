package io.shulie.takin.web.data.result.baseserver;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * @author xingchen
 * @ClassName: BaseServerResult
 * @Package: io.shulie.takin.report.vo
 * @date 2020/7/2717:14
 */
@Measurement(name = "app_base_data")
@Data
public class BaseServerResult {
    /**
     * 时间
     */
    @Column(name = "time")
    private Instant time;

    /**
     * 应用ip
     */
    @Column(name = "app_ip")
    private String appIp;

    /**
     * 应用名
     */
    @Column(name = "app_name")
    private String appName;
    /**
     * cpu使用率
     */
    @Column(name = "cpu_rate")
    private Double cpuRate;
    /**
     * cpuload
     */
    @Column(name = "cpu_load")
    private Double cpuLoad;
    /**
     * 内存使用率
     */
    @Column(name = "mem_rate")
    private Double memRate;
    /**
     * io等待率
     */
    @Column(name = "iowait")
    private Double ioWait;
    /**
     * 网络带宽使用率
     */
    @Column(name = "net_bandwidth_rate")
    private Double netBandWidthRate;
    /**
     * 网络带宽
     */
    @Column(name = "net_bandwidth")
    private Double netBandwidth;
    /**
     * cpu核数
     */
    @Column(name = "cpu_cores")
    private Double cpuCores;
    /**
     * 磁盘大小
     */
    @Column(name = "disk")
    private Double disk;
    /**
     * 内存大小
     */
    @Column(name = "memory")
    private Double memory;

    @Column(name = "young_gc_count")
    private Double youngGcCount;

    @Column(name = "young_gc_time")
    private Double youngGcTime;

    @Column(name = "full_gc_count")
    private Double fullGcCount;
    @Column(name = "full_gc_time")
    private Double fullGcTime;

    /**
     * 应用ip
     */
    @Column(name = "tag_app_ip", tag = true)
    private String tagAppIp;

    /**
     * 应用名
     */
    @Column(name = "tag_app_name", tag = true)
    private String tagAppName;

    /**
     * tag_agent_id
     */
    @Column(name = "tag_agent_id", tag = true)
    private String tagAgentId;

    private Double tps;

    /**
     * 获取毫秒的时间
     *
     * @return
     */
    public long getExtTime() {
        return this.getTime().getEpochSecond() * 1000;
    }
}
