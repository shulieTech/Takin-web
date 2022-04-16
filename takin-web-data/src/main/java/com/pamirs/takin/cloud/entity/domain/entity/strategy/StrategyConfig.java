package com.pamirs.takin.cloud.entity.domain.entity.strategy;

import java.util.Date;

import lombok.Data;

/**
 * @author -
 */
@Data
public class StrategyConfig {
    private Long id;

    private String strategyName;

    private Integer status;

    private Integer isDeleted;

    private Date createTime;

    private Date updateTime;

    private String strategyConfig;
}
