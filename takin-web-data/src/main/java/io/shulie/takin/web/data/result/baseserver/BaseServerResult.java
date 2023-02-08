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
    private Long time;

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
     * tag_agent_id
     */
    @JsonProperty("agent_id")
    private String agentId;

    private Double tps;

    /**
     * 获取毫秒的时间
     * 取到秒的整数
     * @return
     */
    public long getExtTime() {
        if (this.getTime() != null){
            return this.getTime() / 1000 * 1000;
        }
        return 0L;
    }
}
