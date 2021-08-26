package com.pamirs.takin.entity.domain.risk;

import java.io.Serializable;
import java.time.Instant;

import lombok.Data;

/**
* @date 2020/7/22 下午3:14
 */
@Data
public class Metrices implements Serializable {
    /**
     * 时间 单位ms
     */
    private Long time;

    /**
     * tps
     */
    private Double avgTps;
}
