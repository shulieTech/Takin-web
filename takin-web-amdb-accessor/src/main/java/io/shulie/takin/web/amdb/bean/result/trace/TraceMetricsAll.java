/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.web.amdb.bean.result.trace;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TraceMetricsAll implements Serializable {

    private Long time;

    private String edgeId;

    private String clusterTest;

    private String service;

    private String method;

    private String appName;

    private String rpcType;

    private String middlewareName;

    private String tenantAppKey;

    private String envCode;

    private int totalCount;

    private int successCount;

    private int totalRt;

    private int errorCount;

    private int hitCount;

    private int totalTps;

    private int total;

    private int e2eSuccessCount;

    private int e2eErrorCount;

    private int maxRt;

    private BigDecimal avgRt;

    private BigDecimal avgTps;

    private String traceId;

    private String sqlStatement;

    private String logTime;

    private LocalDateTime createDate;

}