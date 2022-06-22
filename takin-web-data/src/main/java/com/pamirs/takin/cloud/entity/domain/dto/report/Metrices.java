package com.pamirs.takin.cloud.entity.domain.dto.report;

import lombok.Data;

/**
 * 应用对象
 *
 * @author qianshui
 * @date 2020/7/22 下午3:14
 */
@Data
public class Metrices {
    /**
     * 时间
     */
    private Long time;

    /**
     * tps
     */
    private Double avgTps;
}
