package com.pamirs.takin.entity.domain.entity.schedule;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class ScheduleRecord {
    private Long id;

    private Long sceneId;

    private Integer podNum;

    private String podClass;

    private Integer status;

    private Integer cpuCoreNum;

    private BigDecimal memorySize;

    private Date createTime;

}
