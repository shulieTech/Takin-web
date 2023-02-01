package io.shulie.takin.web.data.result.baseserver;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author xingchen
 * @ClassName: BaseServerResult
 * @Package: io.shulie.takin.report.vo
 * @date 2020/7/2717:14
 */
@Data
public class BaseServerResult {
    /**
     * 时间
     */
    @JsonProperty("time")
    private Instant time;

    /**
     * 应用ip
     */
    @JsonProperty("app_ip")
    private String appIp;

    /**
     * 应用名
     */
    @JsonProperty("app_name")
    private String appName;
    /**
     * cpu使用率
     */
    @JsonProperty("cpu_rate")
    private Double cpuRate;
    /**
     * cpuload
     */
    @JsonProperty("cpu_load")
    private Double cpuLoad;
    /**
     * 内存使用率
     */
    @JsonProperty("mem_rate")
    private Double memRate;
    /**
     * io等待率
     */
    @JsonProperty("iowait")
    private Double ioWait;
    /**
     * 网络带宽使用率
     */
    @JsonProperty("net_bandwidth_rate")
    private Double netBandWidthRate;
    /**
     * 网络带宽
     */
    @JsonProperty("net_bandwidth")
    private Double netBandwidth;
    /**
     * cpu核数
     */
    @JsonProperty("cpu_cores")
    private Double cpuCores;
    /**
     * 磁盘大小
     */
    @JsonProperty("disk")
    private Double disk;
    /**
     * 内存大小
     */
    @JsonProperty("memory")
    private Double memory;

    /**
     * 应用ip
     */
    @JsonProperty("tag_app_ip")
    private String tagAppIp;

    /**
     * 应用名
     */
    @JsonProperty("tag_app_name")
    private String tagAppName;

    /**
     * tag_agent_id
     */
    @JsonProperty("tag_agent_id")
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
