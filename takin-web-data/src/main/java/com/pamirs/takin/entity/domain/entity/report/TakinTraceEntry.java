package com.pamirs.takin.entity.domain.entity.report;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-08-21
 */
@Data
public class TakinTraceEntry {

    private Long id;

    private String appName;

    private String entry;

    private String method;

    private String status;

    private Long startTime;

    private Long endTime;

    private Long processTime;

    private String traceId;

}
