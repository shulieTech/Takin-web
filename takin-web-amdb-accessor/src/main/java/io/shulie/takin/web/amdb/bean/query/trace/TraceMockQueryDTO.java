package io.shulie.takin.web.amdb.bean.query.trace;

import lombok.Data;

import java.io.Serializable;

@Data
public class TraceMockQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long startTime;

    private Long endTime;

    private String taskId;

    private String tenantAppKey;

    private String envCode;

}
