package io.shulie.takin.web.data.result.perfomanceanaly;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PerformanceBaseDataAll {
    private Long timestamp;

    private Long totalMemory;

    private Long permMemory;

    private Long youngMemory;

    private Long oldMemory;

    private Integer youngGcCount;

    private Integer fullGcCount;

    private Long youngGcCost;

    private Long fullGcCost;

    private BigDecimal cpuUseRate;

    private Long totalBufferPoolMemory;

    private Long totalNoHeapMemory;

    private Integer threadCount;

    private Long baseId;

    private String agentId;

    private String appName;

    private String appIp;

    private String processId;

    private String processName;

    private String envCode;

    private String tenantAppKey;

    private Long tenantId;

    private LocalDateTime createDate;

    private Long time;


}
