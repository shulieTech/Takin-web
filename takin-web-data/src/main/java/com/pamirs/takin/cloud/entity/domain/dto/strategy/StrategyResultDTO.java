package com.pamirs.takin.cloud.entity.domain.dto.strategy;

import lombok.Data;

/**
 * @author 莫问
 * @date 2020-05-15
 */
@Data
public class StrategyResultDTO {

    /**
     * 最小机器数量
     */
    private Integer min;

    /**
     * 最大
     */
    private Integer max;
}
