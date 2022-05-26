package com.pamirs.takin.cloud.entity.domain.entity.schedule;

import java.math.BigDecimal;
import java.util.Date;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author -
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleRecord extends ContextExt {
    private Long id;

    private Long sceneId;

    private Long taskId;

    private Integer podNum;

    private String podClass;

    private Integer status;

    private BigDecimal cpuCoreNum;

    private BigDecimal memorySize;

    private Date createTime;

}