package io.shulie.takin.web.biz.pojo.dto.scene;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EngineMetricsDTO {

    @JsonProperty(value = "time")
    private Long time;

    @JsonProperty(value = "transaction")
    private String transaction;

    @JsonProperty("avg_rt")
    private BigDecimal avgRt;

    @JsonProperty("avg_tps")
    private BigDecimal avgTps;

    @JsonProperty(value = "test_name")
    private String testName;

    private Integer count;

    @JsonProperty("create_time")
    private Integer createTime;

    @JsonProperty("data_num")
    private Integer dataNum;

    @JsonProperty("data_rate")
    private BigDecimal dataRate;

    @JsonProperty("fail_count")
    private Integer failCount;

    @JsonProperty("sent_bytes")
    private Long sentBytes;

    @JsonProperty("received_bytes")
    private Long receivedBytes;

    @JsonProperty("sum_rt")
    private BigDecimal sumRt;

    private BigDecimal sa;

    @JsonProperty("sa_count")
    private Integer saCount;

    @JsonProperty("max_rt")
    private BigDecimal maxRt;

    @JsonProperty("min_rt")
    private BigDecimal minRt;

    @JsonProperty("active_threads")
    private Integer activeThreads;

    @JsonProperty("sa_percent")
    private String saPercent;

    private Integer status;

    @JsonProperty("success_rate")
    private BigDecimal successRate;

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("createDate")
    private LocalDateTime createDate;
}
